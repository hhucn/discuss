(ns discuss.communication
  "Functions concerning the communication with the remote discussion system."
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

(defn init!
  "Initialize initial data from API."
  []
  (let [url (:init config/api)]
    (lib/update-state-item! :layout :add? (fn [_] false))
    (discuss.communication/ajax-get url)))


;; Discussion-related functions
(defn add-start-statement [statement]
  (let [id   (get-in @lib/app-state [:issues :uid])
        slug (get-in @lib/app-state [:issues :slug])
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

(defn prepare-add
  "Save current add-method and show add form."
  [add-type]
  (lib/update-state-item! :layout :add-type (fn [_] add-type))
  (lib/show-add-form))

(defn item-click
  "Dispatch which action has to be done when clicking an item."
  [id url]
  (lib/hide-add-form)
  (cond
    (= id "item_start_statement") (prepare-add :add-start-statement)
    (= id "item_start_premise")   (prepare-add :add-start-premise)
    (= id "item_justify_premise") (prepare-add :add-justify-premise)
    (= url "back") (history/back!) ;; @DEPRECATED
    (= url "add")  (prepare-add "add")
    :else (ajax-get url)))