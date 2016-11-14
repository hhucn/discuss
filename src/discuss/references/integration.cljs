(ns discuss.references.integration
  "Listen for mouse clicks, get references and highlight them in the article."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [om.core :as om]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :refer [split lower-case]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.components.tooltip :as tooltip]
            [discuss.references.lib :as rlib]
            [discuss.views :refer [reference-view]]
            [discuss.config :as config]
            [discuss.communication.main :as com]))

(defn- listen
  "Helper function for mouse-click events."
  [el type]
  (let [out (chan)]
    (events/listen el type (fn [e] (put! out e)))
    out))

;; http://www.thesoftwaresimpleton.com/blog/2014/12/30/core-dot-async-dot-mouse-dot-down/
(defn- save-selected-text
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

(defn- minify-doms
  "Removes dom-elements, which can never be used as a reference."
  [doms]
  (remove #(or (= "script" (lower-case (.-nodeName %)))
               (= "path" (lower-case (.-nodeName %)))
               (= "svg" (lower-case (.-nodeName %)))
               (= "circle" (lower-case (.-nodeName %)))
               (= "html" (lower-case (.-nodeName %)))
               (= "head" (lower-case (.-nodeName %)))
               (= "meta" (lower-case (.-nodeName %)))
               (= "button" (lower-case (.-nodeName %))))
          doms))

(defn- get-parent
  "Assuming that the last occurence of my reference is the closest parent of it, this function will return it."
  [doms ref]
  (last
    (filter
      identity
      (map #(when (lib/substring? ref (lib/trim-all (.-innerHTML %))) %) doms))))


;;; Integrate references and highlight them in the article
(defn- convert-reference
  "Find parent of reference, split it into parts and wrap the original reference for highlighting and interaction."
  [ref]
  (let [ref-text (vlib/html->str (:text ref))
        ref-url (:url ref)
        ref-id (:uid ref)
        doms-raw (.getElementsByTagName js/document "*")
        doms (minify-doms doms-raw)
        parent (get-parent doms ref-text)]
    (when (and parent (not (rlib/highlighted? ref-text)))
      (let [dom-parts (split (.-innerHTML parent) (re-pattern ref-text))
            first-part (first dom-parts)
            last-part (last dom-parts)]
        (om/root reference-view {:text     ref-text
                                 :url      ref-url
                                 :id       ref-id
                                 :dom-pre  first-part
                                 :dom-post (when (and (< 1 (count dom-parts)) (not= last-part ref-text)) last-part)}
                 {:target parent})
        (rlib/highlight! ref-text)))))

(defn process-references
  "Receives references through the API and prepares them for the next steps."
  [refs]
  (when refs
    (doall (map convert-reference refs))))

(defn- references-handler
  "Called when received a response on the reference-query."
  [response]
  (let [res (com/process-response response)
        refs (:references res)]
    (lib/update-state-item! :common :references (fn [_] refs))
    (discuss.references.integration/process-references refs)))

(defn request-references
  "When this app is loaded, request all available references from the external discussion system."
  []
  (let [url (get-in config/api [:get :references])
        headers {"X-Host" js/location.host
                 "X-Path" js/location.pathname}]
    (com/ajax-get url headers references-handler)))