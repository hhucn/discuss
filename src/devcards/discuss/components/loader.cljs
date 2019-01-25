(ns devcards.discuss.components.loader
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.components.loader :as loader]
            [om.next :as om]))

(defcard-om-next loader
  loader/Loader
  parser/reconciler)

(defcard-om-next loader-custom
  loader/Loader
  (om/reconciler {:state {:loader/text "Foo"}
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next loader-button
  loader/LoaderButton
  parser/reconciler)

(defcard-om-next loader-button-custom
  loader/LoaderButton
  (om/reconciler {:state {:loader/text "Foo"}
                  :parser (om/parser {:read parser/read})}))
