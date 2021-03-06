(ns discuss.references.integration
  "Listen for mouse clicks, get references and highlight them in the article."
  (:require [cljs.core.async :refer [go <! timeout]]
            [om.next :as om]
            [clojure.string :refer [lower-case]]
            [clojure.set :refer [difference]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.references.lib :as rlib]
            [discuss.references.main :as rmain]
            [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.utils.logging :as log]))

;;; Include references
(defn- minify-doms
  "Removes dom-elements, which can never be used as a reference."
  [doms]
  (let [tags #{"script" "path" "svg" "circle" "html" "head" "meta" "link" "img"
               "input" "label" "hr" "title" "button" "h1"}]
    (remove #(contains? tags (lower-case (.-nodeName %))) doms)))

(defn- get-parent
  "Assuming that the last occurrence of my reference is the closest parent of
  it, this function will return it."
  [doms ref]
  (last
    (filter
      identity
      (map #(when (lib/substring? ref (lib/trim-and-normalize (.-innerHTML %))) %) doms))))


;;; Integrate references and highlight them in the article
(defn- convert-reference
  "Find parent of reference, split it into parts and wrap the original reference
  for highlighting and interaction."
  [ref]
  (let [ref-text (lib/trim-and-normalize (vlib/html->str (:text ref)))
        ref-url (:url ref)
        ref-id (:uid ref)
        doms-raw (.getElementsByTagName js/document "*")
        doms (minify-doms doms-raw)
        parent (get-parent doms ref-text)]
    (when (and parent (not (rlib/highlighted? ref-text)))
      (let [dom-parts (rlib/split-at-string (lib/trim-and-normalize (.-innerHTML parent)) ref-text)
            first-part (first dom-parts)
            last-part (second dom-parts)]
        (when (= 2 (count dom-parts))
          (om/add-root! (om/reconciler {:state {:text ref-text
                                                :url ref-url
                                                :id ref-id
                                                :dom-pre (vlib/safe-html first-part)
                                                :dom-post (when (and (< 1 (count dom-parts)) (not= last-part ref-text)) (vlib/safe-html last-part))}})
                        rmain/ReferenceView
                        parent)
          (rlib/highlight! ref-text))))))

(defn- process-references!
  "Receives references through the API and prepares them for the next steps."
  [refs]
  (let [refs-on-webpage (lib/load-from-app-state :references/on-webpage)
        refs-to-be-rendered (difference (set refs) (set refs-on-webpage))]
    (doseq [ref refs-to-be-rendered]
      (lib/store-to-app-state! 'references/on-webpage (conj refs-on-webpage ref))
      (convert-reference ref))))

(defn- references-handler
  "Called when received a response on the reference-query."
  [response]
  (let [res (lib/process-response response)
        refs (:references res)]
    (lib/store-to-app-state! 'references/all refs)
    (process-references! refs)))


;; -----------------------------------------------------------------------------

(defn request-references
  "When this app is loaded, request all available references from the external
  discussion system."
  []
  (let [url (get-in config/api [:get :references])
        params {:host js/location.host
                :path js/location.pathname}]
    (log/info "[request-references] Requesting references for %s, %s " url params)
    (comlib/ajax-get url nil references-handler params {:hide-add-form? false})))

(defn auto-refresh-references
  "Periodically queries the current references, to dynamically bind it directly
  into the website."
  []
  (request-references)
  (go (while true
        (<! (timeout 2000))
        (request-references))))