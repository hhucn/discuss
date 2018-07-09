(ns discuss.components.search.statements
  (:require [sablono.core :as html :refer-macros [html]]
            [om.next :as nom :refer-macros [defui]]
            [clojure.walk :refer [keywordize-keys]]
            [cljs.spec.alpha :as s]
            [ajax.core :refer [GET]]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.views :as vlib]
            [discuss.parser :as parser]
            [discuss.utils.common :as lib]
            [discuss.config :as config]))

(s/def ::isPosition boolean?)
(s/def ::content string?)
(s/def ::statementUid pos-int?)
(s/def ::textversions (s/keys :req-un [::content ::statementUid]))

(s/def ::uid (s/or :string string? :int nat-int?))  ;; remove String when new API is released
(s/def ::langUid string?)
(s/def ::issues (s/keys :req-un [::uid ::langUid]))

(s/def ::search-result-from-api
  (s/keys :req-un [::isPosition ::textversions ::issues]))

;; -----------------------------------------------------------------------------
;; New API specification

(s/def ::position? boolean?)
(s/def ::text string?)

(s/def ::slug string?)
(s/def ::lang string?)
(s/def ::title string?)
(s/def ::info string?)
(s/def ::issue
  (s/keys :req-un [::uid ::slug ::lang ::title ::info]))

(s/def ::nickname string?)
(s/def ::author
  (s/keys :req-un [::nickname ::uid]))

(s/def ::search-result-converted
  (s/keys :req-un [::position? ::text ::uid ::issue ::author]))

;; -----------------------------------------------------------------------------

(defn- transform-search-result
  "Transform results from dbas-search to our data structures."
  [{:keys [isPosition textversions issues]}]
  {:position? isPosition
   :text (:content textversions)
   :uid (:statementUid textversions)
   :issue {:uid (:uid issues)
           :slug "Not available"
           :lang (:langUid issues)
           :title "Not available"
           :info "Not available"}
   :author {:nickname "Not available"
            :uid 0}})

(s/fdef transform-search-result
  :args (s/cat :search-result ::search-result-from-api)
  :ret ::search-result-converted)

(defn set-search-results!
  "Store the search results into the app-state."
  [statements]
  (nom/transact! parser/reconciler `[(search/results {:value ~statements})]))

(defn remove-search-results!
  "Reset the search results."
  []
  (set-search-results! {}))

(defn- handle-search-results
  "Handler which is called with the results from ElasticSearch. Extract statements
  from response and write it to the app-state."
  [response]
  (let [results (-> response keywordize-keys :hits :hits)
        data (mapv :_source results)
        statements (filter (partial s/valid? ::search-result-from-api) data)
        converted-stmts (map transform-search-result statements)]
    (set-search-results! converted-stmts)))

(defn search
  "Make a GET request and search in ElasticSearch for the requested data."
  [query]
  (GET (str config/remote-search "_search")
       {:handler handle-search-results
        :params {:q (str query "*")}}))


;; -----------------------------------------------------------------------------

(defui Result
  Object
  (render [this]
          (let [{:keys [text issue author]} (nom/props this)]
            (html [:div.bs-callout.bs-callout-info
                   [:div.row
                    [:div.col-sm-8
                     [:p (vlib/safe-html text)]
                     [:p [:span.btn.btn-sm.btn-primary
                          {:on-click #(println "I want to use this statement:" text) #_#(lib/store-origin! origin)}
                          (t :search :reuse)]]]
                    [:div.col-sm-4
                     [:div.text-right
                      [:span.label.label-default
                       (str (t :common :issue) ": " (:title issue))]
                      [:br]
                      [:span.label.label-default
                       (str (t :search :author) ": " (:nickname author))]]]]]))))
(def ^:private result (nom/factory Result))

(defui Results
  static nom/IQuery
  (query [this] [:search/results])
  Object
  (render [this]
          (let [{:keys [search/results]} (nom/props this)]
            (html [:div.results
                   (take 5
                         (map #(result (merge (lib/unique-react-key-dict) %)) results))]))))
(def results (nom/factory Results))


;; -----------------------------------------------------------------------------

(defui ^:once SearchQuery
  Object
  (render [this]
          (html [:div
                 [:form
                  [:div.form-group
                   [:input.form-control {:type "text"
                                         :on-change #(search (.. % -target -value))}]]]])))
(def search-query (nom/factory SearchQuery))
