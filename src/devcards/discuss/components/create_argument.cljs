(ns devcards.discuss.components.create-argument
  (:require [devcards.core :as dc :refer-macros [defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.components.avatar :as avatar]
            [discuss.components.create-argument :as carg]
            [om.next :as om]
            [discuss.views :as views]))

(defcard-om-next create-argument-with-reference
  carg/CreateArgumentWithReference
  parser/reconciler)

(defcard-om-next main-view
  views/MainView
  parser/reconciler)
