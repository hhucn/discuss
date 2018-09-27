(ns devcards.discuss.add-statements
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [devcards.discuss.utils :as dutils]
            [om.next :as om]
            [discuss.views.add :as vadd]))

(defcard shortcuts
  dutils/shortcuts)

(def sample-api-route "/town-has-to-cut-spending/justify/37/agree?history=/attitude/37")

(defcard-om-next main-view
  views/MainView
  parser/reconciler)

(dc/defcard-doc
  "Do request to the following Route:"
  sample-api-route)

(defcard-om-next add-new-statement
  vadd/StatementForm
  parser/reconciler)

(defcard-om-next add-new-position
  vadd/PositionForm
  parser/reconciler)
