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


;;;; Interaction with integrated references
(defn click-reference
  "When clicking on a reference directly in the text, interact with the user to give her the choice of what
   her next steps might be."
  [reference]
  (lib/change-view! :reference-dialog)
  (rlib/save-selected-reference! reference)
  (sidebar/show)
  (lib/update-state-item! :layout :reference (fn [_] (:text reference))))


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


;;;; Views
(defn create-with-reference-view
  "View containing information about which reference has been chosen and give possibility to find an access point into
   the discussion."
  [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/div #js {:className "text-center"}
                        (dom/h5 nil "Neues Argument mit dieser Referenz erzeugen"))
               (rlib/current-reference-component)
               (om/build find/form-view {})
               (om/build find/results-view data)))))

(defn create-overview
  "Some interaction with the user is necessary to define what kind of statement she wants to add. This view provides an
   entry point for this decision."
  [data]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (dom/div #js {:className "text-center"}
                        (bs/button-primary #(println "btn show issues") "Show Issues")
                        " "
                        (bs/button-primary #(lib/change-view! :reference-create-with-ref) "Jump into the discussion"))))))

(defn dialog-view
  "Show a dialog to give the user the option to choose, whether she wants to get some information about the statement
   or just wants to construct a new statement."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-center"}
               (rlib/current-reference-component)
               (bs/button-primary #(query-reference-details (:id (rlib/get-selected-reference)))
                                  "Wo wird diese Referenz verwendet?")
               " "
               (bs/button-primary #(lib/change-view! :reference-create-with-ref)
                                  "Neues Argument mit Referenz erstellen")))))

(defn usage-view
  "List single usages of reference."
  [data _owner]
  (reify om/IRender
    (render [_]
      (let [issue (:issue data)
            statement (:statement data)
            author (:author data)]
        (dom/div nil
                 (dom/div #js {:className "bs-callout bs-callout-info"}
                          (dom/div #js {:className "pull-right"}
                                   (bs/button-default-sm #(save-statement-change-view data) (vlib/fa-icon "fa-check") " Auswählen"))
                          (dom/a #js {:href    "javascript:void(0)"
                                      :onClick #(save-statement-change-view data)}
                                 (dom/strong nil (:text statement)))
                          (dom/div nil "Issue: " (:title issue))
                          (dom/div nil "Autor: " (:nickname author))))))))

(defn usages-view
  "List with details showing the usages of the given reference."
  []
  (reify om/IRender
    (render [_]
      (let [usages (rlib/get-reference-usages)]
        (dom/div nil
                 (dom/h5 nil "Wo wird diese Referenz verwendet?")
                 (rlib/current-reference-component)
                 (dom/div nil
                          (apply dom/div nil
                                 (map #(om/build usage-view (lib/merge-react-key %)) usages))))))))

(defn agree-disagree-view
  "Agree or disagree with the selected reference."
  []
  (reify om/IRender
    (render [_]
      (let [statement (rlib/get-selected-statement)]
        (dom/div nil
                 (dom/div #js {:className "text-center"}
                          "Stimmen Sie dem Argument zu oder lehnen Sie es ab?")
                 (om/build usage-view statement)
                 (dom/div #js {:className "text-center"}
                          (bs/button-primary #(get-statement-url statement true)
                                             (vlib/fa-icon "fa-thumbs-up")
                                             " Zustimmen")
                          " "
                          (bs/button-primary #(get-statement-url statement false)
                                             (vlib/fa-icon "fa-thumbs-down")
                                             " Ablehnen")))))))
