(ns discuss.devcards.molecules
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.views :as views]))

(defcard-om-next discussion-elements
  views/DiscussionElements
  parser/reconciler)

(defcard-om-next view-dispatcher
  views/ViewDispatcher
  parser/reconciler)

(defcard-om-next main-content-view
  views/MainContentView
  parser/reconciler)

(defcard-om-next main-view
  views/MainView
  parser/reconciler)
