(ns discuss.references
  "Handle interaction with already existing references, for example showing usages in other statements / arguments
   or providing a form to use it in the own statement."
  (:require [om.core :as om]
            [om.dom :as dom]
            [discuss.communication :as com]
            [discuss.config :as config]
            [discuss.sidebar :as sidebar]
            [discuss.utils.common :as lib]))

(defn reference-usage-handler
  "Handler to process information about the reference."
  [response]
  (let [res (com/success-handler response)]))

(defn query-reference-details
  "Show usages of the provided reference."
  [reference-id]
  (let [url (str (:base config/api) (get-in config/api [:get :reference-usages]) "/" reference-id)]
    (com/ajax-get url {} reference-usage-handler)))


;;;; Interaction with integrated references
(defn click-reference
  "When clicking on a reference directly in the text, interact with the user to give her the choice of what
   her next steps might be."
  [ref]
  #_(com/ajax-get url)
  (lib/change-view! :reference-dialog)
  (lib/save-selected-reference! ref)
  (println (lib/selected-reference))
  (sidebar/show)
  (lib/update-state-item! :layout :reference (fn [_] (:text ref))))


;;;; Views
(defn dialog-view
  "Show a dialog to give the user the option to choose, whether she wants to get some information about the statement
   or just wants to construct a new statement."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-center"}
               (dom/button #js {:className "btn btn-primary"
                                :onClick   #(query-reference-details 4)}
                           "Find usages of this reference")
               " "
               (dom/button #js {:className "btn btn-primary"}
                           "Create new Statement with this reference")))))

(defn usages-view
  "List with details showing the usages of the given reference."
  []
  (reify om/IRender
    (render [_]
      (dom/h4 nil "foo"))))