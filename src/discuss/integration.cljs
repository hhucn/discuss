(ns discuss.integration
  "Listen for mouse clicks, get references and highlight them in the article."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [om.core :as om]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :refer [split lower-case]]
            [discuss.extensions]
            [discuss.communication :as com]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.sidebar :as sidebar]
            [discuss.tooltip :as tooltip]))

;;; Listener for mouse clicks
(defn get-mouse-position
  "Multi browser support for getting the current mouse position. Returns tuple of x and y: [x y]"
  [e]
  (if (.-pageX e)
    [(.-pageX e) (.-pageY e)]
    [(.-clientX e) (.-clientY e)]
    ;(let [x (- (+ (.-clientX e) (.. js/document -body -scrollLeft)) (.. js/document -documentElement -scrollLeft))
    ;      y (- (+ (.-clientY e) (.. js/document -body -scrollTop)) (.. js/document -documentElement -scrollTop))]
    ;  [x y (.-clientX e) (.-clientY e)])
    ))

(defn listen
  "Helper function for mouse-click events."
  [el type]
  (let [out (chan)]
    (events/listen el type (fn [e]
                             (put! out e)
                             (lib/save-mouse-position (get-mouse-position e))))
    out))

;; http://www.thesoftwaresimpleton.com/blog/2014/12/30/core-dot-async-dot-mouse-dot-down/
(defn save-selected-text
  "Get the users selection and save it."
  []
  (let [selection (str (.getSelection js/window))]
    (if (and (pos? (count selection))
             (not= selection (lib/get-selection)))
      (do (tooltip/move-to-selection)
          (lib/update-state-item! :user :selection (fn [_] selection)))
      (tooltip/hide))))

(let [clicks (listen (.getElementById js/document "discuss-text") "click")]
  (go (while true
        (<! clicks)
        (save-selected-text))))

(defn minify-doms
  "Removes dom-elements, which can never be used as a reference."
  [doms]
  (remove #(or (= "script" (lower-case (.-nodeName %)))
               (= "path"   (lower-case (.-nodeName %)))
               (= "svg"    (lower-case (.-nodeName %)))
               (= "circle" (lower-case (.-nodeName %)))
               (= "html"   (lower-case (.-nodeName %)))
               (= "head"   (lower-case (.-nodeName %)))
               (= "meta"   (lower-case (.-nodeName %)))
               (= "button" (lower-case (.-nodeName %))))
          doms))

(defn get-parent
  "Assuming that the last occurence of my reference is the closest parent of it,
   this function will return it."
  [doms ref]
  (last
    (filter
      identity
      (map #(when (lib/substring? ref (.-innerHTML %)) %) doms))))


;;; Integrate references and highlight them in the article
(defn convert-reference
  "Find parent of reference, split it into parts and wrap the original reference for highlighting and interaction."
  [ref]
  (let [ref-text (vlib/html->str (:text ref))
        ref-url  (:url ref)
        doms-raw (.getElementsByTagName js/document "*")
        doms     (minify-doms doms-raw)
        parent   (get-parent doms ref-text)]
    (when parent
      (let [dom-parts (split (.-innerHTML parent) (re-pattern ref-text))]
        (om/root discuss.views/reference-view {:text     ref-text
                                               :url      ref-url
                                               :dom-pre  (first dom-parts)
                                               :dom-post (last dom-parts)}
                 {:target parent})))))

(defn process-references
  "Receives references through the API and prepares them for the next steps."
  [refs]
  (doall (map convert-reference refs)))


;;; Interaction with integratet references
(defn click-reference
  "When clicking on a reference directly in the text, make AJAX request with the url to the discussion
   and show the sidebar to start the discussion."
  [text url]
  (com/ajax-get url)
  (sidebar/show)
  (lib/update-state-item! :layout :reference (fn [_] text)))