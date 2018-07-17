(ns devcards.discuss.add-statements
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [devcards.discuss.utils :as dutils]
            [om.next :as om]))

(defcard shortcuts dutils/shortcuts)

(def sample-api-route "/town-has-to-cut-spending/justify/37/agree?history=/attitude/37")

(def ^:private stmts-reconciler
  (om/reconciler {:state (assoc @(om/app-state parser/reconciler) :api/last-call sample-api-route)
                  :parser (om/parser {:read parser/read :mutate parser/mutate})}))

(defcard-om-next main-content-view
  views/MainContentView
  parser/reconciler)

(defcard-om-next add-new-statement
  views/AddElement
  parser/reconciler)
