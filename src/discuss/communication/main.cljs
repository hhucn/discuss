(ns discuss.communication.main
  "Functions concerning the communication with the remote discussion system."
  (:require [ajax.core :refer [GET POST]]
            [goog.string :refer [htmlEscape]]
            [discuss.config :as config]
            [discuss.utils.common :as lib]
            [discuss.references.integration :as rint]
            [discuss.communication.lib :as comlib]
            [cljs.spec.alpha :as s]
            [discuss.components.search.statements :as search]))

;;;; Calls
(defn process-url-handler
  "React on response after sending a new statement. Reset atom and call newly
  received url."
  [response]
  (let [res (lib/process-response response)
        url (:url res)]
    (lib/remove-origin!)
    (search/remove-search-results!)
    (lib/hide-add-form!)
    (lib/update-state-item! :layout :add-type (fn [_] nil))
    (rint/request-references)
    (comlib/ajax-get url)))

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
   (POST (comlib/make-url url)
         {:body            (lib/clj->json body)
          :handler         handler
          :error-handler   comlib/error-handler
          :format          :json
          :response-format :json
          :headers         headers
          :keywords?       true}))
  ([url body handler]
   (post-json url body handler {"Content-Type" "application/json"}))
  ([url body]
   (post-json url body process-url-handler {"Content-Type" "application/json"})))

(defn- post-statement [statement reference origin add-type]
  (let [app @lib/app-state
        url (get-in config/api [:add add-type])
        headers (merge {"Content-Type" "application/json"} (comlib/token-header))
        body {:statement     (htmlEscape statement)
              :reference     (htmlEscape reference)
              :origin        origin
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
  ([statement reference origin]
   (let [action (get-in @lib/app-state [:layout :add-type])
         statement (or (:content origin) statement)]
     (case action
       :add-start-statement (post-statement statement reference origin :add-start-statement)
       :add-start-premise (post-statement [statement] reference origin :add-start-premise)
       :add-justify-premise (post-statement [statement] reference origin :add-justify-premise)
       (println "Action not found:" action))))
  ([statement reference] (dispatch-add-action statement reference nil)))

(s/fdef dispatch-add-action
        :args (s/cat :statement string? :reference string? :origin map?))

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
    :else (comlib/ajax-get url)))


;;;; Get things started!
(defn init-with-references!
  "Load discussion and initially get reference to include them in the discussion."
  []
  (rint/request-references)
  (comlib/init!))

(defn resend-last-api
  "Resends stored url from last api call."
  {:deprecated 0.4}
  []
  (comlib/ajax-get (lib/get-last-api)))
