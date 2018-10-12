(ns devcards.discuss.components.search
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [cljs.test :refer [testing is are]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [om.next :as om]
            [discuss.components.search.statements :as search]))

(def ^:private two-search-results
  [{:position? true
    :text "we should shut down University Park"
    :uid 37
    :issue {:uid "1"
            :slug "Not available"
            :lang "en"
            :title "Not available"
            :info "Not available"}
    :author {:nickname "Not available"
             :uid 0}}
   {:position? false
    :text "the city is planing a new park in the upcoming month"
    :uid 52
    :issue {:uid "1"
            :slug "Not available"
            :lang "en"
            :title "Not available"
            :info "Not available"}
    :author {:nickname "Not available"
             :uid 0}}])

(defcard-om-next main-view
  views/MainView
  parser/reconciler)

(defcard-om-next search-results-view-hardcoded-values
  search/Results
  (om/reconciler {:state {:search/results two-search-results}
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next search-form
  search/SearchQuery
  parser/reconciler)

(defcard-om-next search-results-dynamic
  search/Results
  parser/reconciler)
