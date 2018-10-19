(ns discuss.components.search.statements
  (:require [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [go chan alts! >! <!]]
            [clojure.set :refer [rename-keys]]
            [om.next :as om :refer-macros [defui]]
            [cljs.spec.alpha :as s]
            [ajax.core :refer [GET]]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.views :as vlib]
            [discuss.utils.common :as lib]
            [discuss.communication.lib :as comlib]
            [discuss.utils.logging :as log]
            [discuss.parser :as parser]))

(s/def ::uid integer?)
(s/def ::text string?)
(s/def ::nickname string?)
(s/def ::author (s/keys :req-un [::uid ::nickname]))

(s/def ::slug string?)
(s/def ::lang string?)
(s/def ::title string?)
(s/def ::info string?)
(s/def ::issue (s/keys :req-un [::uid ::slug ::lang ::title ::info]))

(s/def ::position? boolean?)

(s/def ::search-result
  (s/keys :req-un [::uid ::text ::author ::issue ::position?]))

(s/def ::search-results
  (s/coll-of ::search-result))


;; -----------------------------------------------------------------------------

(defonce search-channel (chan))

(defn- valid-statement?
  "Check whether the results from the API are correct and log them if not."
  [statement]
  (if (s/valid? ::search-result statement)
    statement
    (log/debug "Received invalid statement: " statement)))
(s/fdef valid-statement?
  :args (s/cat :statement ::search-result?))

(defn- set-search-results!
  "Store the search results into the app-state."
  [statements]
  (let [rename-position (map #(rename-keys % {:isPosition :position?}))
        validate-statements (map valid-statement?)
        xform-statements (comp rename-position validate-statements)
        validated-statements (transduce xform-statements conj statements)]
    (om/transact! parser/reconciler `[(search/results {:value ~validated-statements})])))

(defn remove-search-results!
  "Reset the search results."
  []
  (set-search-results! []))

(defn- handle-search-results
  "Handler which is called with the results from ElasticSearch. Extract statements
  from response and write it to the app-state."
  [response]
  (-> response
      lib/json->clj
      :results
      set-search-results!))

(defn- query->server
  "Make a GET request and search in ElasticSearch for the requested data."
  [query]
  (GET (comlib/make-url "/search")
       {:handler handle-search-results
        :params {:q (str query "*")}}))
(s/fdef query->server
  :args (s/cat :query string?))

(defn- search-async
  "Infinite loop waiting for search queries."
  []
  (go (while true
        (query->server (<! search-channel)))))

(defn search
  "Take input and put it on the search channel."
  [query]
  (go (>! search-channel query)))
(s/fdef search
  :args (s/cat :query string?))

(defn entrypoint
  "Entrypoint to start the search module."
  []
  (search-async))
(entrypoint)

;; -----------------------------------------------------------------------------

(defui Result
  Object
  (render [this]
          (let [{:keys [text issue author]} (om/props this)]
            (html [:div.bs-callout.bs-callout-info
                   [:div.row
                    [:div.col-sm-8
                     [:p (vlib/safe-html text)]
                     [:p [:span.btn.btn-sm.btn-primary
                          {:on-click #(println "I want to use this statement:" text)}
                          (t :search :reuse)]]]
                    [:div.col-sm-4
                     [:div.text-right
                      [:span.label.label-default
                       (str (t :common :issue) ": " (:title issue))]
                      [:br]
                      [:span.label.label-default
                       (str (t :search :author) ": " (:nickname author))]]]]]))))
(def ^:private result (om/factory Result))

(defui Results
  static om/IQuery
  (query [this] [:search/results])
  Object
  (render [this]
          (let [{:keys [search/results]} (om/props this)]
            (html [:div.results
                   (take 5
                         (map #(result (merge (lib/unique-react-key-dict) %)) results))]))))
(def results (om/factory Results))


;; -----------------------------------------------------------------------------

(defui ^:once SearchQuery
  Object
  (render [this]
          (html [:div
                 [:form
                  [:div.form-group
                   [:input.form-control {:type "text"
                                         :on-change #(search (.. % -target -value))}]]]])))
(def search-query (om/factory SearchQuery))
