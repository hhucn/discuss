(ns discuss.references
  "Handle interaction with already existing references, for example showing usages in other statements / arguments
   or providing a form to use it in the own statement."
  (:require [om.core :as om]
            [om.dom :as dom]
            [discuss.communication :as com]
            [discuss.config :as config]
            [discuss.sidebar :as sidebar]
            [discuss.utils.common :as lib]))

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
  (lib/save-selected-reference! reference)
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
               (dom/button #js {:className "btn btn-primary"
                                :onClick   #(query-reference-details (:id (lib/selected-reference)))}
                           "Find usages of this reference")
               " "
               (dom/button #js {:className "btn btn-primary"}
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
                             :onClick #(com/ajax-get-and-change-view (:url statement) :default)}
                        (dom/strong nil (:text statement)))
                 (dom/div nil "Issue: " (:title issue))
                 (dom/div nil "Author: " (:nickname author)))))))

(defn usages-view
  "List with details showing the usages of the given reference."
  []
  (reify om/IRender
    (render [_]
      (let [usages (get-reference-usages-from-app-state)]
        (dom/div nil
                 (apply dom/div nil
                        (map #(om/build usage-view (lib/merge-react-key %)) usages)))))))