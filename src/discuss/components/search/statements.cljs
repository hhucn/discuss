(ns discuss.components.search.statements
  (:require [sablono.core :as html :refer-macros [html]]
            [om.core :as om]
            [om.next :as nom :refer-macros [defui]]
            [clojure.walk :refer [keywordize-keys]]
            [cljs.spec.alpha :as s]
            [ajax.core :refer [GET]]
            [discuss.specs :as specs]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.views :as vlib]
            [discuss.parser :as parser]
            [discuss.utils.common :as lib]
            [discuss.config :as config]
            [om.dom :as dom]))

(defn set-search-results!
  "Store the search results into the app-state."
  [statements]
  (lib/update-state-item! :search :results (fn [_] statements))
  (nom/transact! parser/reconciler `[(search/results {:results ~statements})]))

(defn remove-search-results!
  "Reset the search results."
  []
  (set-search-results! {}))

(defn- search-results
  "Handler which is called with the results from ElasticSearch. Extract statements
  from response and write it to the app-state."
  [response]
  (let [results (-> response keywordize-keys :hits :hits)
        data (mapv :_source results)
        statements (filter (partial s/valid? ::specs/statement) data)]
    (set-search-results! statements)))

(defn search
  "Make a GET request and search in ElasticSearch for the requested data."
  [query]
  (GET (str config/remote-search "_search")
       {:handler search-results
        :params {:q (str query "*")}}))


;; -----------------------------------------------------------------------------

(defn result-html [origin]
  (let [{:keys [author content aggregate-id]} origin]
    (when-not (empty? content)
      (html [:div.bs-callout.bs-callout-info
             [:div.row
              [:div.col-sm-8
               [:p (vlib/safe-html content)]
               [:p [:span.btn.btn-sm.btn-primary
                    {:on-click #(lib/store-origin! origin)}
                    (t :search :reuse)]]]
              [:div.col-sm-4
               [:div.text-right
                [:span.label.label-default
                 (str (t :search :origin) ": " aggregate-id)]
                [:br]
                [:span.label.label-default
                 (str (t :search :author) ": " author)]]]]]))))

(defui Result
  Object
  (render [this]
          (result-html (nom/props this))))
(def ^:private result (nom/factory Result))

(defn- result-now
  "Show one result of the search engine."
  [data _owner] (reify om/IRender (render [_] (result-html data))))

(defui Results
  static nom/IQuery
  (query [this] [:search/results])
  Object
  (render [this]
          (let [{:keys [search/results]} (nom/props this)]
            (html [:div.results (map #(result (merge (lib/unique-react-key-dict) %)) results)]))))
(def results (nom/factory Results))

(defn results-now
  "Show results from the search."
  []
  (reify om/IRender
    (render [_]
      (let [results (get-in @lib/app-state [:search :results])]
        (apply dom/div nil
               (map #(om/build result-now % (lib/unique-key-dict)) results))))))

;; -----------------------------------------------------------------------------

(def search-for-statement
  (html [:div
         [:form
          [:div.form-group
           [:input.form-control {:type "text"
                                 :on-change #(search (.. % -target -value))}]]]]))

(defui ^:once SearchQuery
  Object
  (render [this] search-for-statement))
(def search-query (nom/factory SearchQuery))

(defn search-query-now
  "Create form to select issue and place the search."
  [_ _] (reify om/IRender (render [_] search-for-statement)))
