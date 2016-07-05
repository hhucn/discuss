(ns discuss.references.main
  "Handle interaction with already existing references, for example showing usages in other statements / arguments
   or providing a form to use it in the own statement."
  (:require [om.core :as om]
            [om.dom :as dom]
            [discuss.communication :as com]
            [discuss.config :as config]
            [discuss.find :as find]
            [discuss.sidebar :as sidebar]
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
(defn- current-reference-component
  "Return DOM element showing which reference is currently selected."
  []
  (dom/div #js {:style #js {:paddingBottom "1em"}}
           "You have selected: " (:text (rlib/get-selected-reference))))

(defn create-overview
  "Some interaction with the user is necessary to define what kind of statement she wants to add. This view provides an
   entry point for this decision."
  [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               "Overview"))))

(defn create-with-reference-view
  "View containing information about which reference has been chosen and give possibility to find an access point into
   the discussion."
  [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/div #js {:className "text-center"}
                        (dom/h5 nil "Create new Statement with Reference"))
               (current-reference-component)
               (om/build find/form-view {})
               (om/build find/results-view data)))))

(defn dialog-view
  "Show a dialog to give the user the option to choose, whether she wants to get some information about the statement
   or just wants to construct a new statement."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-center"}
               (current-reference-component)
               (bs/button-primary #(query-reference-details (:id (rlib/get-selected-reference)))
                                  "Find usages of this reference")
               " "
               (bs/button-primary #(lib/change-view! :reference-create)
                                  "Create new Statement with this reference")))))

(defn usage-view
  "List single usages of reference."
  [data _owner]
  (reify om/IRender
    (render [_]
      (let [reference (:reference data)
            issue (:issue data)
            statement (:statement data)
            author (:author data)]
        (dom/div nil
                 (dom/div #js {:className "bs-callout bs-callout-info"}
                          (dom/a #js {:href    "javascript:void(0)"
                                      :onClick #(save-statement-change-view data)}
                                 (dom/strong nil (:text statement)))
                          (dom/div nil "Reference: \"" (:title reference) "\"")
                          (dom/div nil "Issue: " (:title issue))
                          (dom/div nil "Author: " (:nickname author))))))))

(defn usages-view
  "List with details showing the usages of the given reference."
  []
  (reify om/IRender
    (render [_]
      (let [usages (rlib/get-reference-usages)
            ref-title (:title (:reference (first usages)))]
        (dom/div nil
                 (dom/div #js {:className "text-center"}
                          "Usages of reference: "
                          ref-title)
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
                          "Do you agree or disagree with this statement?")
                 (om/build usage-view statement)
                 (dom/div #js {:className "text-center"}
                          (bs/button-primary #(get-statement-url statement true)
                                             (vlib/fa-icon "fa-thumbs-up")
                                             " Agree")
                          " "
                          (bs/button-primary #(get-statement-url statement false)
                                             (vlib/fa-icon "fa-thumbs-down")
                                             " Disagree")))))))
