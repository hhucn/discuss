(ns discuss.references.main
  "Handle interaction with already existing references, for example showing usages in other statements / arguments
   or providing a form to use it in the own statement."
  (:require [om.core :as om]
            [om.dom :as dom]
            [discuss.communication.main :as com]
            [discuss.config :as config]
            [discuss.find :as find]
            [discuss.components.sidebar :as sidebar]
            [discuss.references.lib :as rlib]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn save-statement-change-view
  "Saves the current selected statement (or the only one if there is only one available) and changes to
   the view to configure own attitude."
  [statement]
  (rlib/save-selected-statement! statement)
  (lib/change-view! :reference-agree-disagree))


;;;; Handlers & Queries
(defn reference-usage-handler
  "Handler to process information about the reference. Store results and change view."
  [response]
  (let [res (com/process-response response)]
    (lib/update-state-item! :common :reference-usages (fn [_] res))
    (lib/change-view! :reference-usages)))

(defn query-reference-details
  "Show usages of the provided reference."
  [reference-id]
  (let [url (str (:base config/api) (get-in config/api [:get :reference-usages]) "/" reference-id)]
    (com/ajax-get url {} reference-usage-handler)))

(defn get-statement-handler
  "Processes response and changes view with given url."
  [response]
  (let [res (com/process-response response)]
    (com/ajax-get-and-change-view (:url res) :default)))

(defn get-statement-url
  "Given an issue-id, statement-id and attitude, query statement url inside the discussion."
  [statement agree]
  (let [issue-id (get-in statement [:issue :uid])
        statement-id (get-in statement [:statement :uid])
        pre-url (get-in config/api [:get :statement-url])
        url (clojure.string/join "/" [pre-url issue-id statement-id agree])]
    (com/ajax-get url {} get-statement-handler)))


;;;; Interaction with integrated references
(defn click-reference
  "When clicking on a reference directly in the text, interact with the user to give her the choice of what
   her next steps might be."
  [reference]
  (rlib/save-selected-reference! reference)
  (query-reference-details (:id reference))
  (sidebar/show)
  (lib/update-state-item! :layout :reference (fn [_] (:text reference))))


;;;; Views
(defn create-with-reference-view
  "View containing information about which reference has been chosen and give possibility to find an access point into
   the discussion."
  [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h5 #js {:className "text-center"} "Finde Aussage in der Diskussion")
               (om/build rlib/current-reference-component {})
               (om/build find/form-view {})
               (om/build find/results-view data)))))

(defn create-overview
  "Some interaction with the user is necessary to define what kind of statement she wants to add. This view provides an
   entry point for this decision."
  []
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "text-center"}
               (bs/button-primary #(println "btn show issues") "Show Issues")
               " "
               (bs/button-primary #(lib/change-view! :reference-create-with-ref) "Jump into the discussion")))))

(defn dialog-view
  "Show a dialog to give the user the option to choose, whether she wants to get some information about the statement
   or just wants to construct a new statement."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-center"}
               (om/build rlib/current-reference-component {})
               (bs/button-primary #(query-reference-details (:id (rlib/get-selected-reference)))
                                  "Wo wird diese Referenz verwendet?")
               " "
               (dom/button #js {:className "btn btn-primary"
                                :onClick   #(lib/change-view! :reference-create-with-ref)}
                           "Springe in die Diskussion")))))

(defn usage-list-view
  "List single usages of reference."
  [data]
  (reify om/IRender
    (render [_]
      (let [issue (:issue data)
            argument (first (:arguments data))
            author (:author data)]
        (bs/callout-info
          (dom/div #js {:className "pull-right"}
                   (bs/button-default-sm #(com/jump-to-argument (:slug issue) (:uid argument)) (vlib/fa-icon "fa-check") " Auswählen"))
          (dom/a #js {:href    "javascript:void(0)"
                      :onClick #(com/jump-to-argument (:slug issue) (:uid argument))}
                 (dom/strong nil (:text argument)))         ; TODO this should not be only the first one
          (dom/div nil "Issue: " (:title issue))
          (dom/div nil "Autor: " (:nickname author)))))))

(defn usages-view
  "List with details showing the usages of the given reference."
  []
  (reify om/IRender
    (render [_]
      (let [usages (rlib/get-reference-usages)]
        (dom/div nil
                 (dom/h5 nil "An welchen Stellen wird dieser Textausschnitt verwendet?")
                 (om/build rlib/current-reference-component {})
                 (apply dom/div nil
                        (map #(om/build usage-list-view (lib/merge-react-key %)) usages)))))))