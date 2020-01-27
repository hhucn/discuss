(ns discuss.references.main
  "Handle interaction with already existing references, for example showing
  usages in other statements / arguments or providing a form to use it in the
  own statement."
  (:require [om.next :as om :refer-macros [defui]]
            [clojure.spec.alpha :as s]
            [goog.string :refer [format]]
            [goog.string.format]
            [sablono.core :as html :refer-macros [html]]
            [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.references.lib :as rlib]
            [discuss.texts.references :as textref]
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
(defn- reference-usage-handler
  "Handler to process information about the reference. Store results and change
  view."
  [response]
  (let [res (lib/process-response response)]
    (rlib/save-reference-usages! res)
    (lib/change-view! :reference-usages)))

(defn query-reference-details
  "Show usages of the provided reference."
  [reference]
  (let [url (format "%s/%d" (get-in config/api [:get :reference-usages]) (:id reference))]
    (log/info "Requesting information for reference with id %d" (:id reference))
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
  static om/IQuery
  (query [this] [:uid :author :issue :conclusion :premise])
  Object
  (render [this]
    (let [{:keys [uid author issue texts is_supportive]} (om/props this)
          intro (textref/reference-usage-intro (:nickname author) (:conclusion texts) (:premise texts) (:attacks texts) is_supportive)]
      (html [:div.bs-callout.bs-callout-info
             [:div.pull-right
              (bs/button-default-sm #(comlib/jump-to-argument (:slug issue) uid)
                                    (vlib/fa-icon "fa-comment-o"))]
             [:a {:href "javascript:void(0)"
                  :onClick #(comlib/jump-to-argument (:slug issue) uid)}
              [:strong (vlib/safe-html intro)]]
             [:div (t :common :author) ": " (:nickname author)]
             [:div (t :common :issue) ": " (:title issue)]]))))
(def reference-usage-for-single-argument (om/factory ReferenceUsageForSingleArgumentView {:keyfn :uid}))

(defui ReferenceUsagesForArgumentsView
  static om/IQuery
  (query [this] [:arguments])
  Object
  (render [this]
    (let [{:keys [arguments]} (om/props this)]
      (html [:div
             (map reference-usage-for-single-argument arguments)]))))
(def reference-usages-for-arguments (om/factory ReferenceUsagesForArgumentsView {:keyfn :arguments}))

(defui UsagesView
  "Complete list of all references and all their usages in their arguments."
  static om/IQuery
  (query [this]
    [:references/usages :references/selected])
  Object
  (render [this]
    (let [{:keys [references/usages references/selected]} (om/props this)]
      (html [:div
             (if-not (empty? selected)
               [:div
                (vlib/view-header (t :references :usages/view-heading))
                [:p.text-center (t :references :usages/lead)]
                [:div.text-center
                 [:p [:em.text-info (format "\"%s\"" (:text selected))]]
                 [:div.btn.btn-primary {:onClick (fn [_e]
                                                   (lib/save-selection! (:text selected))
                                                   (lib/change-view! :create/argument))}
                  (t :create/argument :short)]]
                (when (seq usages)
                  [:div
                   [:hr]
                   [:p.text-center (t :references :usages/list) "."]
                   (reference-usages-for-arguments usages)])]
               [:div.text-center
                (vlib/view-header (t :references :usages/not-found-lead))
                [:p (t :references :usages/not-found-body) "."]])]))))
(def usages-view-next (om/factory UsagesView))

(defui ReferenceView
  "Nested reference link in text."
  Object
  (render [this]
    (let [{:keys [text url id dom-pre dom-post]} (om/props this)]
      (html [:span
             [:span dom-pre]
             [:span.arguments.pointer {:onClick #(click-reference (Reference. id text url))}
              text " " (vlib/logo)]
             [:span dom-post]]))))
(def reference (om/factory ReferenceView))
