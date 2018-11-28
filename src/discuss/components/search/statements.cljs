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
(s/def ::author (s/keys :req-un [::nickname]
                        :opt-un [::uid]))

(s/def ::slug string?)
(s/def ::lang string?)
(s/def ::title string?)
(s/def ::info string?)
(s/def ::issue (s/keys :req-un [::uid ::slug ::lang ::title ::info]))

(s/def ::position? boolean?)

(s/def ::aggregate-id string?)
(s/def ::entity-id number?)
(s/def ::version pos-int?)

(s/def ::identifier
  (s/keys :req-un [::aggregate-id ::entity-id ::version]))

(s/def ::search-result
  (s/keys :req-un [::text ::author]
          :opt-un [::uid ::issue ::position? ::identifier]))

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
    (log/debug "Received" (count validated-statements) "search results from D-BAS instance")
    (om/transact! parser/reconciler `[(search/results {:value ~validated-statements})])))

(defn remove-search-results!
  "Reset the search results."
  []
  (set-search-results! []))

(defn remove-selected-search-result!
  "Remove currently selected search result."
  []
  (om/transact! parser/reconciler `[(search/selected {:value ~nil})]))

(defn remove-all-search-related-results-and-selections
  "Clear list of results and currently selected search result."
  []
  (om/transact! parser/reconciler `[(search/selected {:value ~nil})
                                    (search/results {:value []})]))

(defn- handle-dbas-search-results
  "Handler which is called with the results from ElasticSearch. Extract statements
  from response and write it to the app-state."
  [response]
  (-> response lib/json->clj :results set-search-results!))

(defn- convert-single-eden-statement
  "Getting results from EDEN, convert one statement to match local specs."
  [statement]
  (let [identifier (:identifier statement)
        content (:content statement)]
    {:text (:text content)
     :author {:nickname (:author content)}
     :identifier {:aggregate-id (:aggregate-id identifier)
                  :entity-id (int (:entity-id identifier))
                  :version (:version identifier)}}))

(defn- set-eden-search-results!
  "Convert data from EDEN aggregators to match the local data structure."
  [statements]
  (let [conform-statements (map convert-single-eden-statement)
        validate-statements (map valid-statement?)
        xform-statements (comp conform-statements validate-statements)
        validated-statements (transduce xform-statements conj statements)]
    (log/debug "Received" (count validated-statements) "search results from EDEN instance")
    (om/transact! parser/reconciler `[(search/results {:value ~validated-statements})])))

(defn- handle-eden-search-results
  "Handler which is called with the results from ElasticSearch. Extract statements
  from response and write it to the app-state."
  [response]
  (-> response lib/json->clj :statements set-eden-search-results!))

(defn- query->server
  "Make a GET request and search in ElasticSearch for the requested data."
  [query]
  (if (lib/host-eden)
    (GET (str (lib/host-eden) "/statements/contain")
         {:handler handle-eden-search-results
          :params {:search-string query}})
    (GET (comlib/make-url "/search")
         {:handler handle-dbas-search-results
          :params {:q (str query "*")}})))
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
          (let [{:keys [text issue author identifier] :as search-result} (om/props this)
                aggregator (:aggregate-id identifier)]
            (html [:div.bs-callout.bs-callout-info
                   [:div.row
                    [:div.col-sm-8
                     [:p (vlib/safe-html text)]
                     [:p [:span.btn.btn-sm.btn-primary
                          {:on-click #(om/transact! parser/reconciler `[(search/selected {:value ~search-result})
                                                                        (search/results {:value []})])}
                          (t :search :reuse)]]]
                    [:div.col-sm-4
                     [:div.text-rights
                      (when issue
                        [:span.label.label-default
                         (str (t :common :issue) ": " (:title issue))
                         [:br]])
                      (when aggregator
                        [:span.label.label-default
                         (str (t :search :origin) ": " aggregator)
                         [:br]])
                      [:span.label.label-default
                       (str (t :search :author) ": " (:nickname author))]]]]]))))
(def result (om/factory Result))

(defui Results
  static om/IQuery
  (query [this] [:search/results])
  Object
  (render [this]
          (let [{:keys [search/results]} (om/props this)]
            (html [:div {:className (lib/prefix-name "search-results")}
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
