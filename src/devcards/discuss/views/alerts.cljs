(ns devcards.discuss.views.alerts
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [sablono.core :refer-macros [html]]
            [discuss.parser :as parser]
            [discuss.views.alerts :as valerts]
            [discuss.utils.common :as lib]
            [om.next :as om]))

(defcard add-errors
  (html [:div
         [:button.btn.btn-primary {:onClick #(lib/error! "Razupaltuff")} "Add \"Razupaltuff\""]
         " "
         [:button.btn.btn-primary {:onClick #(lib/error! nil)} "Remove error"]]))

(defcard-om-next error-view-global-app-state
  valerts/ErrorAlert
  parser/reconciler)

(defcard-om-next error-view-has-errors
  valerts/ErrorAlert
  (om/reconciler {:state (merge @(om/app-state parser/reconciler)
                                {:layout/error "This is an error message"})
                  :parser (om/parser {:read parser/read})}))
