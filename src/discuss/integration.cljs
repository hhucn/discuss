(ns discuss.integration
  "Listen for mouse clicks, get references and highlight them in the article."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.dom :as gdom]
            [goog.events :as events]
            [om.core :as om]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :as string]
            [discuss.lib :as lib]
            [discuss.extensions])
  (:import [goog.dom]))

(defn get-selection
  "Return the stored selection of the user."
  []
  (get-in @lib/app-state [:user :selection]))

(defn has-selection? []
  (> (count (get-selection)) 0))

(defn save-selected-text
  "Get the users selection and save it."
  []
  (let [selection (str (.getSelection js/window))]
    (when (and (> (count selection) 0)
               (not= selection (get-selection)))
      (lib/update-state-item! :user :selection (fn [_] selection)))))


;;; Listener for mouse clicks
(defn listen
  "Helper function for mouse-click events."
  [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

;; http://www.thesoftwaresimpleton.com/blog/2014/12/30/core-dot-async-dot-mouse-dot-down/
(let [clicks (listen (.getElementById js/document "discuss-text") "click")]
  (go (while true
        (<! clicks)
        (save-selected-text))))

(defn minify-doms
  "Removes dom-elements, which can never be used as a reference."
  [doms]
  (remove #(or (= "SCRIPT" (.-nodeName %))
               (= "path"   (.-nodeName %))
               (= "svg"    (.-nodeName %))
               (= "circle" (.-nodeName %))
               (= "HTML"   (.-nodeName %))
               (= "HEAD"   (.-nodeName %))
               (= "META"   (.-nodeName %))
               (= "BUTTON" (.-nodeName %)))
          doms))

(defn get-parent
  "Assuming that the last occurence of my reference is the closest parent of it,
   this function will return it."
  [doms ref]
  (last
    (filter
      identity
      (map (fn [dom]
             (when (lib/substring? ref (.-innerHTML dom))
               dom))
           doms))))


;;; Register arguments after they have been highlighted and prepared
(defn register-arguments
  "Takes collection of all arguments in DOM and binds it to a view."
  [arguments]
  (loop [argument (first arguments)
         col      (rest arguments)]
    (when-not (nil? argument)
      (om/root discuss.views/argument-view lib/app-state {:target argument})
      (recur (first col) (rest col)))))


;;; Integrate references and highlight them in the article
(defn convert-reference
  "Find parent of reference, split it into parts and wrap the original reference for highlighting and interaction."
  [ref]
  (let [ref-text  (:text ref)
        doms-raw  (.getElementsByTagName js/document "*")
        doms      (minify-doms doms-raw)
        parent    (get-parent doms ref-text)
        dom-parts (string/split (.-innerHTML parent) (re-pattern ref-text))
        dom-ref   (str "<span class='arguments'>" ref-text "</span><span class='arguments-toggle'></span>")
        new-dom   (str (first dom-parts) dom-ref (last dom-parts))]
    (when parent
      (set! (.-innerHTML parent) new-dom))))

(defn process-references
  "Receives references through the API and prepares them for the next steps."
  [refs]
  (doall (map #(convert-reference %) refs))
  (let [arguments (.getElementsByClassName js/document "arguments-toggle")]
    (register-arguments arguments)))