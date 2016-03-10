(ns discuss.communication
  (:require [ajax.core :refer [GET POST]]
            [clojure.walk :refer [keywordize-keys]]
            [cognitect.transit :as transit]
            [discuss.config :as config]
            [discuss.debug :as debug]
            [discuss.history :as history]
            [discuss.lib :as lib]))

;; Auxiliary functions
(def r (transit/reader :json))

(defn make-url
  "Add prefix if not provided."
  [url]
  (str (:host config/api) url))

(defn token-header
  "Return token header for ajax request if user is logged in."
  []
  (when (lib/logged-in?)
    {"X-Messaging-Token" (lib/get-token)}))

;; Handlers
(defn error-handler
  "Generic error handler for ajax requests."
  [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text))
  (lib/loading? false))

(defn ajax-get
  "Make ajax call to dialogue based argumentation system."
  [url]
  (debug/update-debug :last-api url)
  (GET (make-url url)
       {:handler lib/update-all-states!
        :headers (token-header)
        :error-handler error-handler})
  (lib/loading? true))

(defn success-handler [response]
  (let [res (keywordize-keys (transit/read r response))
        error (:error res)
        url (:url res)]
    (if (< 0 (count error))
      (.log js/console error)
      (do
        (lib/hide-add-form)
        (ajax-get url)))))


;; Discussion-related functions
(defn add-start-statement [statement]
  (let [id   (get-in @lib/discussion-state [:issues :uid])
        slug (get-in @lib/discussion-state [:issues :slug])
        url  (str (:base config/api) (get-in config/api [:add :start_statement]))]
    (debug/update-debug :last-api url)
    (POST (make-url url)
          {:body            (lib/clj->json {:statement statement
                                            :issue_id id
                                            :slug slug})
           :handler         success-handler
           :error-handler   error-handler
           :format          :json
           :response-format :json
           :headers         (merge {"Content-Type" "application/json"}
                                   (token-header))
           :keywords?       true})))

(defn item-click
  "Dispatch which action has to be done when clicking an item."
  [id url]
  (lib/hide-add-form)
  (cond
    (= url "back") (history/back!)
    (= url "add")  (lib/show-add-form)
    (= id "item_start_statement") (lib/show-add-form)
    (= id "item_start_premise")   (lib/show-add-form)
    (= id "item_justify_premise") (lib/show-add-form)
    :else (ajax-get url)))