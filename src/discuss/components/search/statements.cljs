(ns discuss.components.search.statements
  (:require [sablono.core :as html :refer-macros [html]]
            [om.next :as om :refer-macros [defui]]
            [clojure.walk :refer [keywordize-keys]]
            [cljs.spec.alpha :as s]
            [goog.crypt.base64 :as b64]
            [ajax.core :refer [GET]]
            [discuss.specs :as specs]
            [discuss.utils.views :as vlib]
            [discuss.parser :as parser]))

(defn- search-results
  "Handler which is called with the results from ElasticSearch. Extract statements
  from response and write it to the app-state."
  [response]
  (let [results (-> response keywordize-keys :hits :hits)
        data (mapv :_source results)
        statements (filter (partial s/valid? ::specs/statement) data)]
    (om/transact! parser/reconciler `[(search/results {:results ~statements})])))

(defn search
  "Make a GET request and search in ElasticSearch for the requested data."
  [query]
  (GET "http://localhost:9200/_search"
       {:handler search-results
        :headers {"Authorization" (str "Basic " (b64/encodeString "elastic:changeme"))}
        :params {:q (str query "*")}}))

(defui Result
  Object
  (render [this]
          (let [{:keys [author content aggregate-id]} (om/props this)]
            (when-not (empty? content)
              (html [:div.bs-callout.bs-callout-info
                     [:span.badge.pull-right aggregate-id]
                     [:p (vlib/safe-html content)]
                     [:p (str "Author: " author)]])))))
(def ^:private result (om/factory Result))

(defui ^:once Results
  static om/IQuery
  (query [this] [:search/results])
  Object
  (render [this]
          (let [{:keys [search/results]} (om/props this)]
            (html [:div.results (map #(result (merge {:keyfn :id} %)) results)]))))
(def results (om/factory Results))

(defui ^:once SearchQuery
  Object
  (render [this]
          (html [:div
                 [:form
                  [:div.form-group
                   [:label "Search for Statements"]
                   [:input.form-control {:type "text"
                                         :on-change #(search (.. % -target -value))}]]]])))
(def search-query (om/factory SearchQuery))
