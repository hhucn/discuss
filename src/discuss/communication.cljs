(ns discuss.communication
  "Functions concerning the communication with the remote discussion system."
  (:require [ajax.core :refer [GET POST]]
            [clojure.walk :refer [keywordize-keys]]
            [cognitect.transit :as transit]
            [discuss.config :as config]
            [discuss.debug :as debug]
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
  (lib/error-msg! (str status " " status-text))
  (lib/loading? false))

(defn ajax-get
  "Make ajax call to dialogue based argumentation system."
  [url]
  (debug/update-debug :last-api url)
  (lib/no-error!)
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
      (lib/error-msg! error)
      (do
        (lib/no-error!)
        (lib/hide-add-form)
        (lib/update-state-item! :layout :add-type (fn [_] nil))
        (ajax-get url)))))

(defn init!
  "Initialize initial data from API."
  []
  (let [url (:init config/api)]
    (lib/update-state-item! :layout :add? (fn [_] false))
    (discuss.communication/ajax-get url)))


;; Discussion-related functions
(defn get-conclusion-id
  "Returns statement id to which the newly added statement is referred to.
   Currently this is stored in the data_statement_uid of the first bubble."
  []
  (let [bubble (first (lib/get-bubbles))]
    (:data_statement_uid bubble)))

(defn post-statement [statement reference add-type]
  (let [id            (get-in @lib/app-state [:issues :uid])
        slug          (get-in @lib/app-state [:issues :slug])
        conclusion-id (get-conclusion-id)                   ; Relevant for add-start-premise
        supportive?   (get-in @lib/app-state [:discussion :is_supportive])
        arg-uid       (get-in @lib/app-state [:discussion :arg_uid]) ; For premisses for arguments
        attack-type   (get-in @lib/app-state [:discussion :attack_type])
        origin        js/location.href
        url           (str (:base config/api) (get-in config/api [:add add-type]))]
    (POST (make-url url)
          {:body            (lib/clj->json {:statement statement
                                            :reference reference
                                            :conclusion_id conclusion-id
                                            :supportive supportive?
                                            :arg_uid arg-uid
                                            :attack_type attack-type
                                            :origin origin
                                            :issue_id id
                                            :slug slug})
           :handler         success-handler
           :error-handler   error-handler
           :format          :json
           :response-format :json
           :headers         (merge {"Content-Type" "application/json"}
                                   (token-header))
           :keywords?       true})))

(defn dispatch-add-action
  "Check which action needs to be performed based on the type previously stored in the app-state."
  [statement reference]
  (let [action (get-in @lib/app-state [:layout :add-type])]
    (cond
      (= action :add-start-statement) (post-statement statement reference :add-start-statement)
      (= action :add-start-premise)   (post-statement [statement] reference :add-start-premise)
      (= action :add-justify-premise) (post-statement [statement] reference :add-justify-premise)
      :else (println "Action not found:" action))))

(defn prepare-add
  "Save current add-method and show add form."
  [add-type]
  (lib/update-state-item! :layout :add-type (fn [_] add-type))
  (lib/show-add-form))

(defn item-click
  "Prepare which action has to be done when clicking an item."
  [id url]
  (lib/hide-add-form)
  (cond
    (= id "item_start_statement") (prepare-add :add-start-statement)
    (= id "item_start_premise")   (prepare-add :add-start-premise)
    (= id "item_justify_premise") (prepare-add :add-justify-premise)
    (= url "add")  (prepare-add "add")
    :else (ajax-get url)))