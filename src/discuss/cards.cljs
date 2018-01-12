(ns discuss.cards
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om defcard-om-next dom-node]]
            [sablono.core :as html :refer-macros [html]]
            [om.next :as om :refer-macros [defui]]
            [discuss.parser :as parser]
            [discuss.components.search.statements :refer [SearchQuery Results]]
            [discuss.utils.common :as lib]
            [discuss.views :as views]
            [discuss.communication.auth :as auth]
            [discuss.components.search.statements :as search]))

(enable-console-print!)

(defcard options
  (html [:div.btn.btn-primary {:onClick #(auth/login "Christian" "iamgroot")} "Login"]))

(defcard-om discuss
  views/main-view
  lib/app-state)

(defonce test-data {:foo :bar
                    :search/results [:foo :bar :baz]})

(defonce devcard-reconciler
  (om/reconciler {:state @lib/app-state
                  :parser (om/parser {:read parser/read :mutate parser/mutate})}))

#_(defcard discuss-next
  (dom-node
   (fn [_ node]
     (om/add-root! parser/reconciler views/main-view-next node)))
  {:inspect-data true})

#_(defcard search-query
  (dom-node
   (fn [_ node]
     (om/add-root! parser/reconciler SearchQuery node)))
  {:inspect-data true})

(defcard-om search-query-now
  search/search-query-now
  lib/app-state)

(defcard search-results
  (dom-node
   (fn [_ node]
     (om/add-root! parser/reconciler Results node)))
  {:inspect-data true})

(defcard-om search-results-now
  search/results-now
  lib/app-state)

;; -----------------------------------------------------------------------------
;; Start devcards

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (when-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (html [:div "This is working"]) node)))
(main)
