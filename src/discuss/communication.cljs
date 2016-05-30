(ns discuss.communication
  "Functions concerning the communication with the remote discussion system."
  (:require [ajax.core :refer [GET POST]]
            [clojure.walk :refer [keywordize-keys]]
            [discuss.config :as config]
            [discuss.debug :as debug]
            [discuss.utils.common :as lib]))

;;; Auxiliary functions
(defn make-url
  "Prefix url with host."
  [url]
  (str (:host config/api) url))

(defn token-header
  "Return token header for ajax request if user is logged in."
  []
  (when (lib/logged-in?)
    {"X-Messaging-Token" (lib/get-token)}))


;;; Handlers
(defn error-handler
  "Generic error handler for ajax requests."
  [{:keys [status status-text]}]
  (.log js/console (str "I feel a disturbance in the Force... " status " " status-text))
  (lib/error-msg! (str status " " status-text))
  (lib/loading? false))

(defn ajax-get
  "Make ajax call to dialogue based argumentation system."
  ([url headers handler]
   (debug/update-debug :last-api url)
   (lib/no-error!)
   (GET (make-url url)
        {:handler       handler
         :headers       (merge (token-header) headers)
         :error-handler error-handler})
   (lib/loading? true))
  ([url headers]
   (ajax-get url headers lib/update-all-states!))
  ([url]
   (ajax-get url {})))

(defn success-handler [response]
  (let [res (lib/json->clj response)
        url (:url res)
        error (:error res)]
    (if (pos? (count error))
      (lib/error-msg! error)
      (do
        (lib/no-error!)
        (lib/hide-add-form)
        (lib/update-state-item! :layout :add-type (fn [_] nil))
        (ajax-get url)))))

(defn references-handler
  "Called when received a response on the reference-query."
  [response]
  (let [res (keywordize-keys response)
        refs (:references res)
        error (:error res)]
    (if (pos? (count error))
      (lib/error-msg! error)
      (do
        (lib/no-error!)
        (lib/update-state-item! :common :references (fn [_] refs))
        (discuss.integration/process-references refs)))))

(defn init!
  "Initialize initial data from API."
  []
  (let [url (:init config/api)]
    (lib/update-state-item! :layout :add? (fn [_] false))
    (discuss.communication/ajax-get url)))


;;;; Discussion-related functions
(defn get-conclusion-id
  "Returns statement id to which the newly added statement is referred to.
   Currently this is stored in the data_statement_uid of the first bubble."
  []
  (let [bubble (first (lib/get-bubbles))]
    (:data_statement_uid bubble)))

;;; POST functions
(defn post-json
  "Wrapper to prepare a POST request. Sending and receiving JSON."
  ([url body handler headers]
   (POST (make-url url)
         {:body            (lib/clj->json body)
          :handler         handler
          :error-handler   error-handler
          :format          :json
          :response-format :json
          :headers         headers
          :keywords?       true}))
  ([url body handler]
   (post-json url body handler {"Content-Type" "application/json"}))
  ([url body]
   (post-json url body success-handler {"Content-Type" "application/json"})))

(defn request-references
  "When this app is loaded, request all available references from the external discussion system."
  []
  (let [url "api/get/references"                            ; TODO hardcoded url = bad
        headers {"X-Host" js/location.host
                 "X-Path" js/location.pathname}]
    (ajax-get url headers references-handler)))

(defn post-statement [statement reference add-type]
  (let [url (str (:base config/api) (get-in config/api [:add add-type]))
        headers (merge {"Content-Type" "application/json"} (token-header))
        body {:statement     statement
              :reference     reference
              :conclusion_id (get-conclusion-id)            ; Relevant for add-start-premise
              :supportive    (get-in @lib/app-state [:discussion :is_supportive])
              :arg_uid       (get-in @lib/app-state [:discussion :arg_uid]) ; For premisses for arguments
              :attack_type   (get-in @lib/app-state [:discussion :attack_type])
              :host          js/location.host
              :path          js/location.pathname
              :issue_id      (get-in @lib/app-state [:issues :uid])
              :slug          (get-in @lib/app-state [:issues :slug])}]
    (post-json url body success-handler headers)))

;;; For preparation
(defn dispatch-add-action
  "Check which action needs to be performed based on the type previously stored in the app-state."
  [statement reference]
  (let [action (get-in @lib/app-state [:layout :add-type])]
    (cond
      (= action :add-start-statement) (post-statement statement reference :add-start-statement)
      (= action :add-start-premise) (post-statement [statement] reference :add-start-premise)
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
    (= id "item_start_premise") (prepare-add :add-start-premise)
    (= id "item_justify_premise") (prepare-add :add-justify-premise)
    (= url "add") (prepare-add "add")
    (= url "login") (lib/change-view! :login)
    :else (ajax-get url)))