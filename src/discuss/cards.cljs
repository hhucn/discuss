(ns discuss.cards
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om defcard-om-next dom-node]]
            [sablono.core :as html :refer-macros [html]]
            [om.next :as om :refer-macros [defui]]
            [discuss.parser :as parser]
            [discuss.components.search.statements :refer [SearchQuery]]
            [discuss.utils.common :as lib]
            [discuss.views :as views]))

(enable-console-print!)

(defcard-om discuss
  views/main-view
  lib/app-state)

(defonce test-data {:foo :bar
                    :search/results [:foo :bar :baz]})

(defonce devcard-reconciler
  (om/reconciler {:state test-data
                  :parser (om/parser {:read parser/read :mutate parser/mutate})}))

(defcard search-query-card-no-next
  (dom-node
   (fn [_ node]
     (om/add-root! devcard-reconciler SearchQuery node)))
  {:inspect-data true})


;; -----------------------------------------------------------------------------
;; Start devcards

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (when-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (html [:div "This is working"]) node)))
(main)
