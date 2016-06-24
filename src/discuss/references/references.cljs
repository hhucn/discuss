(ns discuss.references.main
  "Handle interaction with already existing references, for example showing usages in other statements / arguments
   or providing a form to use it in the own statement."
  (:require [om.core :as om]
            [om.dom :as dom]
            [discuss.communication :as com]
            [discuss.config :as config]
            [discuss.sidebar :as sidebar]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn save-selected-reference!
  "Saves the currently clicked reference for further processing."
  [ref]
  (lib/update-state-item! :reference-usages :selected-reference (fn [_] ref)))

(defn selected-reference
  "Returns the currently selected reference."
  []
  (get-in @lib/app-state [:reference-usages :selected-reference]))

(defn save-selected-statement!
  "Saves the currently selected statement for further processing."
  [statement]
  (lib/update-state-item! :reference-usages :selected-statement (fn [_] statement)))

(defn selected-statement
  "Returns the currently selected statement from reference usages."
  []
  (get-in @lib/app-state [:reference-usages :selected-statement]))

(defn- get-reference-usages-from-app-state
  "Return list of reference usages, which were previously stored in the app-state.
   TODO: optimize"
  []
  (get-in @lib/app-state [:common :reference-usages]))

(defn reference-usage-handler
  "Handler to process information about the reference. Store results and change view."
  [response]
  (let [res (com/success-handler response)]
    (lib/update-state-item! :common :reference-usages (fn [_] res))
    (lib/change-view! :reference-usages)))

(defn query-reference-details
  "Show usages of the provided reference."
  [reference-id]
  (let [url (str (:base config/api) (get-in config/api [:get :reference-usages]) "/" reference-id)]
    (com/ajax-get url {} reference-usage-handler)))


;;;; Interaction with integrated references
(defn click-reference
  "When clicking on a reference directly in the text, interact with the user to give her the choice of what
   her next steps might be."
  [reference]
  (lib/change-view! :reference-dialog)
  (save-selected-reference! reference)
  (sidebar/show)
  (lib/update-state-item! :layout :reference (fn [_] (:text reference))))


;;;; Views
(defn dialog-view
  "Show a dialog to give the user the option to choose, whether she wants to get some information about the statement
   or just wants to construct a new statement."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-center"}
               (bs/button-primary #(query-reference-details (:id (selected-reference)))
                                  "Find usages of this reference")
               " "
               (bs/button-primary nil
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
        (dom/div #js {:className "bs-callout bs-callout-info"}
                 (dom/a #js {:href    "javascript:void(0)"
                             :onClick (fn [_]
                                        (save-selected-statement! data)
                                        (lib/change-view! :reference-agree-disagree))}
                        (dom/strong nil (:text statement)))
                 (dom/div nil "Issue: " (:title issue))
                 (dom/div nil "Author: " (:nickname author)))))))

(defn usages-view
  "List with details showing the usages of the given reference."
  []
  (reify om/IRender
    (render [_]
      (let [usages (get-reference-usages-from-app-state)
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
      (let [statement (selected-statement)]
        (dom/div nil
                 (dom/div #js {:className "text-center"}
                          "Do you agree or disagree with this statement?")
                 (om/build usage-view statement)
                 (dom/div #js {:className "text-center"}
                          (bs/button-primary #(println "foo")
                                             (vlib/fa-icon "fa-thumbs-up")
                                             " Agree")
                          " "
                          (bs/button-primary nil
                                             (vlib/fa-icon "fa-thumbs-down")
                                             " Disagree")))))))
