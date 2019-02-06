(ns discuss.references.main
  "Handle interaction with already existing references, for example showing
  usages in other statements / arguments or providing a form to use it in the
  own statement."
  (:require [om.core :as om]
            [om.dom :as dom]
            [om.next :as nom :refer-macros [defui]]
            [clojure.spec.alpha :as s]
            [sablono.core :as html :refer-macros [html]]
            [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.references.lib :as rlib]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.utils.logging :as log]))

(defrecord Reference [id text url])

(s/def ::id pos-int?)
(s/def ::reference
  (s/and
   #(instance? Reference %)
   (s/keys :req-un [::id ::comlib/text ::comlib/url])))

;;;; Handlers & Queries
(defn reference-usage-handler
  "Handler to process information about the reference. Store results and change
  view."
  [response]
  (let [res (lib/process-response response)]
    (rlib/save-reference-usages! res)
    (lib/change-view! :reference-usages)))

(defn query-reference-details
  "Show usages of the provided reference."
  [reference]
  (let [url (str (get-in config/api [:get :reference-usages]) "/" (:id reference))]
    (log/info "Requesting information for reference with id" (:id reference))
    (comlib/ajax-get url nil reference-usage-handler)))

(s/fdef query-reference-details
  :args (s/cat :reference ::reference))


;;;; Interaction with integrated references
(defn click-reference
  "When clicking on a reference directly in the text, interact with the user to
  give her the choice of what her next steps might be."
  [reference]
  (rlib/save-selected-reference! reference)
  (query-reference-details reference)
  (lib/show-overlay))

(s/fdef click-reference
  :args (s/cat :reference ::reference))


;; -----------------------------------------------------------------------------

(defui ReferenceUsageForSingleArgumentView
  "Build single usage of a reference in an argument."
  static nom/IQuery
  (query [this]
         `[:argument])
  Object
  (render [this]
          (let [{:keys [argument]} (nom/props this)
                {:keys [author issue]} argument]
            (html [:div.bs-callout.bs-callout-info
                   [:div.pull-right
                    (bs/button-default-sm
                     #(comlib/jump-to-argument (:slug issue) (:uid argument))
                     (vlib/fa-icon "fa-search"))]
                   [:a {:href "javascript:void(0)"
                        :onClick #(comlib/jump-to-argument (:slug issue) (:uid argument))}
                    [:strong (vlib/safe-html (:text argument))]]
                   [:div (t :common :author) ": " (:nickname author)]
                   [:div (t :common :issue) ": " (:title issue)]]))))
(def reference-usage-for-single-argument (nom/factory ReferenceUsageForSingleArgumentView {:keyfn identity}))

(defui ReferenceUsagesForArgumentsView
  static nom/IQuery
  (query [this]
         `[:arguments])
  Object
  (render [this]
          (let [{:keys [issue arguments author]} (nom/props this)]
            (html [:div
                   (map
                    #(reference-usage-for-single-argument
                      {:author author
                       :issue issue
                       :argument %})
                    arguments)]))))
(def reference-usages-for-arguments (nom/factory ReferenceUsagesForArgumentsView {:keyfn identity}))

(defui UsagesView
  "Complete list of all references and all their usages in their arguments."
  static nom/IQuery
  (query [this]
         `[:references/usages])
  Object
  (render [this]
          (let [{:keys [references/usages]} (nom/props this)]
            (html [:div
                   (vlib/view-header (t :references :usages/view-heading))
                   ;; TODO: Add current reference
                   (map reference-usages-for-arguments usages)
                   ]))))
(def usages-view-next (nom/factory UsagesView))

(defui ReferenceView
  "Nested reference link in text."
  Object
  (render [this]
          (let [{:keys [text url id dom-pre dom-post]} (nom/props this)]
            (html [:span
                   [:span dom-pre]
                   [:span.arguments.pointer {:onClick #(click-reference (Reference. id text url))}
                    text " " (vlib/logo)]
                   [:span dom-post]]))))
(def reference (nom/factory ReferenceView))
