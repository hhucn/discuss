(ns discuss.communication.main
  "Functions concerning the communication with the remote discussion system."
  (:require [ajax.core :refer [GET POST]]
            [goog.string :refer [htmlEscape]]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.string :refer [join]]
            [discuss.config :as config]
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


;;;; Handlers
(defn error-handler
  "Generic error handler for ajax requests."
  [{:keys [status status-text response]}]
  (lib/log (str "I feel a disturbance in the Force... " status " " status-text))
  (lib/loading? false))

(defn success-handler-next-view
  "After the successful ajax call, change the view to the previously saved next view."
  [response]
  (lib/change-to-next-view!)
  (lib/update-all-states! response))


;;;; Calls
(defn ajax-get
  "Make ajax call to dialog based argumentation system."
  ([url headers handler params]
   (lib/no-error!)
   (lib/last-api! url)
   (lib/loading? true)
   (GET (make-url url)
        {:handler       handler
         :headers       (merge (token-header) headers)
         :params        params
         :error-handler error-handler}))
  ([url headers handler] (ajax-get url headers handler nil))
  ([url headers] (ajax-get url headers lib/update-all-states!))
  ([url] (ajax-get url {})))

(defn ajax-get-and-change-view
  "Make ajax call to jump right into the discussion and change to discussion view."
  [url view]
  (lib/next-view! view)
  (ajax-get url {} success-handler-next-view))

(defn process-url-handler
  "React on response after sending a new statement. Reset atom and call newly received url."
  [response]
  (let [res (lib/process-response response)
        url (:url res)]
    (lib/hide-add-form!)
    (lib/update-state-item! :layout :add-type (fn [_] nil))
    ((resolve 'discuss.references.integration/request-references))
    (ajax-get url)))

;;;; Discussion-related functions
(defn get-conclusion-id
  "Returns statement id to which the newly added statement is referred to.
   Currently this is stored in the data_statement_uid of the first bubble."
  []
  (let [bubble (first (lib/get-bubbles))]
    (:data_statement_uid bubble)))


;;;; POST functions
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
   (post-json url body process-url-handler {"Content-Type" "application/json"})))

(defn post-statement [statement reference add-type]
  (let [app @lib/app-state
        url (get-in config/api [:add add-type])
        headers (merge {"Content-Type" "application/json"} (token-header))
        body {:statement     (htmlEscape statement)
              :reference     (htmlEscape reference)
              :conclusion_id (get-conclusion-id)            ; Relevant for add-start-premise
              :supportive    (get-in app [:discussion :is_supportive])
              :arg_uid       (get-in app [:discussion :arg_uid]) ; For premisses for arguments
              :attack_type   (get-in app [:discussion :attack_type])
              :host          js/location.host
              :path          js/location.pathname
              :issue_id      (get-in app [:issues :uid])
              :slug          (get-in app [:issues :slug])}]
    (post-json url body process-url-handler headers)))


;;;; For preparation
(defn dispatch-add-action
  "Check which action needs to be performed based on the type previously stored in the app-state."
  [statement reference]
  (let [action (get-in @lib/app-state [:layout :add-type])]
    (case action
      :add-start-statement (post-statement statement reference :add-start-statement)
      :add-start-premise (post-statement [statement] reference :add-start-premise)
      :add-justify-premise (post-statement [statement] reference :add-justify-premise)
      (println "Action not found:" action))))

(defn prepare-add
  "Save current add-method and show add form."
  [add-type]
  (lib/update-state-item! :layout :add-type (fn [_] add-type))
  (lib/show-add-form!))

(defn item-click
  "Prepare which action has to be done when clicking an item."
  [id url]
  (lib/hide-add-form!)
  (cond
    (= id "item_start_statement") (prepare-add :add-start-statement)
    (= id "item_start_premise") (prepare-add :add-start-premise)
    (= id "item_justify_premise") (prepare-add :add-justify-premise)
    (= url "add") (prepare-add "add")
    (= url "login") (lib/change-view! :login)
    :else (ajax-get url)))


;;;; Get things started!
(defn init!
  "Request initial data from API."
  []
  (let [url (:init config/api)]
    (lib/update-state-item! :layout :add? (fn [_] false))
    (ajax-get-and-change-view url :default)))

(defn init-with-references!
  "Load discussion and initially get reference to include them in the discussion."
  []
  ((resolve 'discuss.references.integration/request-references))
  (init!))

(defn resend-last-api
  "Resends stored url from last api call."
  []
  (ajax-get (lib/get-last-api)))

(defn jump-to-argument
  "Jump directly into the discussion to let the user argue about the given argument.

   ** TODO: Update route **"
  [slug arg-id]
  (let [url (join "/" ["api" slug "jump" arg-id])]
    (ajax-get-and-change-view url :discussion)))
