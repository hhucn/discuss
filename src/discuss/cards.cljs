(ns discuss.cards
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om.next :as nom :refer-macros [defui]]
            [discuss.parser :as parser]
            [discuss.utils.common :as lib]
            [discuss.views :as views])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-om defcard-om-next deftest]]))

(enable-console-print!)

(defcard-om discuss
  views/main-view
  lib/app-state)

(defui Foo
  Object
  (render [this]
          (html [:h1 "Wuki"])))
(def foo (nom/factory Foo))

(defcard-om-next wuki-card
  foo
  parser/reconciler)


;; -----------------------------------------------------------------------------
;; Start devcards

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (when-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (html [:div "This is working"]) node)))

(main)
