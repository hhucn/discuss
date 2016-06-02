(ns discuss.references
  "Handle interaction with already existing references, for example showing usages in other statements / arguments
   or providing a form to use it in the own statement."
  (:require [om.core :as om]
            [om.dom :as dom]
            [discuss.communication :as com]
            [discuss.config :as config]))

(defn reference-usage-handler
  "Handler to process information about the reference."
  [response]
  ;; TODO write common wrapper for handlers
  )

(defn show-usages
  "Show usages of the provided reference.
   @TODO"
  [reference]
  (let [url (str (:base config/api) (get-in config/api [:get :reference-usages]) "/" reference)]
    (com/ajax-get url {} reference-usage-handler)))

(defn dialog
  "Show a dialog to give the user the option to choose, whether she wants to get some information about the statement
   or just wants to construct a new statement."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-center"}
               (dom/button #js {:className "btn btn-primary"}
                           "Find usages of this reference")
               " "
               (dom/button #js {:className "btn btn-primary"}
                           "Create new Statement with this reference")))))