(ns discuss.communication.lib
  "Helper-functions for the communication component."
  (:require [clojure.string :as str]
            [cljs.spec.alpha :as s]
            [cljs.reader]
            [ajax.core :refer [GET POST]]
            [goog.string :refer [format]]
            [goog.string.format]
            [discuss.communication.bubble-replacements :as breps]
            [discuss.communication.specs :as comspecs]
            [discuss.config :as config]
            [discuss.history.discussion :as hdis]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.logging :as log]
            [discuss.communication.connectivity :as comcon]))

;;;; Auxiliary functions
(defn make-url
  "Prefix url with host."
  [url]
  (str (lib/host-dbas) url))

(defn token-header
  "Return token header for ajax request if user is logged in."
  []
  (when (lib/logged-in?)
    {"X-Authentication" (lib/clj->json {:type "user" :nickname (lib/get-nickname) :token (lib/get-token)})}))

(defn login-or-add-item
  "Check if user is logged in. If not, present item to login. Else present item
  which triggers the add-form."
  [logged-in?]
  (let [translation (if logged-in? (t :discussion :add-position) (t :login :item))
        url (if logged-in? "add" "login")]
    {:htmls [translation]
     :texts [translation]
     :url url}))

(defn process-and-set-items-and-bubbles
  "Receive response and prepare UI with the new items and bubbles for the next
  step in the discussion.

  Dispatch the current add-step, which is the new expected statement or
  position, which is to be added in the next step."
  [response]
  (let [{:keys [bubbles attitudes items attacks positions]} (lib/process-response response)
        add-step (if (seq positions) :add/position :add/statement)
        update-items (cond
                       (seq items) items
                       (seq positions) (conj positions (login-or-add-item (lib/logged-in?)))
                       (seq attacks) (vals attacks)
                       (seq attitudes) (vals attitudes)
                       :default [])
        bubbles' (-> bubbles breps/replace-congratulation-bubbles breps/replace-profile-link-bubbles)]
    (lib/store-multiple-values-to-app-state! [['discussion/bubbles bubbles']
                                              ['discussion/items update-items]
                                              ['discussion/add-step add-step]])))


;;;; Handlers
(defn error-handler
  "Generic error handler for ajax requests."
  [{:keys [status status-text response]}]
  (log/error "Request failed with status code %s (%s)" status status-text)
  (lib/error! (str/join (str " " (t :common :and) " ") (map :name (:errors response)))))

(defn index-handler
  [response]
  (lib/change-to-next-view!)
  (process-and-set-items-and-bubbles response))

(defn ajax-get
  "Make ajax call to dialog based argumentation system."
  ([url headers handler params]
   (log/debug "GET Request to: %s" (make-url url))
   (lib/hide-add-form!)
   (GET (make-url url)
        {:handler       handler
         :headers       (merge (token-header) headers)
         :params        params
         :error-handler error-handler}))
  ([url headers handler] (ajax-get url headers handler nil))
  ([url headers] (ajax-get url headers process-and-set-items-and-bubbles))
  ([url] (ajax-get url (token-header))))

(defn ajax-get-and-change-view
  "Make ajax call to jump right into the discussion and change to discussion
  view."
  ([url view handler]
   (lib/next-view! view)
   (ajax-get url {} handler))
  ([url view]
   (ajax-get-and-change-view url view index-handler)))

(defn do-post
  "Get prepared statements and simply fire a POST request."
  ([request-url body handler error-handler headers]
   (log/info (str "Posting " (dissoc body :password) " to " request-url))
   (POST request-url
         {:body            (lib/clj->json body)
          :handler         handler
          :error-handler   error-handler
          :format          :json
          :response-format :json
          :headers         (merge (token-header) headers)
          :keywords?       true}))
  ([request-url body handler error-handler]
   (do-post request-url body handler error-handler {"Content-Type" "application/json"})))

(defn jump-to-argument
  "Jump directly into the discussion to let the user argue about the given
  argument."
  [slug arg-id]
  (let [base-jump (:jump config/api)
        with-slug (str/replace base-jump #":slug" (str slug))
        with-arg (str/replace with-slug #":argument-id" (str arg-id))]
    (hdis/save-discussion-urls! [with-arg])
    (ajax-get-and-change-view with-arg :discussion)))

(defn init!
  "Request initial data from API. Optionally provide a slug to change the
  discussion."
  ([]
   (init! (:init config/api)))
  ([slug]
   (log/fine (format "Initializing discussion: %s" (:init config/api)))
   (hdis/save-discussion-urls! [slug])
   (ajax-get-and-change-view slug :default index-handler)))
(s/fdef init!
  :args (s/? (s/cat :slug string?)))

(defn discussion-step
  "Receives the next step in the discussion and performs a GET request to this
  url."
  [url]
  (lib/store-to-app-state! 'search/results [])
  (ajax-get url (token-header) process-and-set-items-and-bubbles))
(s/fdef discussion-step
  :args (s/cat :url string?))

(defn item-click
  "Store the currently clicked url in the app-state."
  [url]
  (hdis/save-discussion-url! url)
  (discussion-step url))
(s/fdef item-click
  :args (s/cat :url string?))

(s/fdef process-and-set-items-and-bubbles
  :args (s/cat :response ::comspecs/response))

(s/fdef login-or-add-item
  :args (s/cat :logged-in? boolean?)
  :ret ::comspecs/item)

(defn set-remote-service-config!
  "Query remote configuration of dbas and eden instances and store them in the
  app-state."
  []
  (let [url (:services/configuration config/api)]
    (when (and (string? url)
               (seq url))
      (log/info "Querying service information from %s" url)
      (GET url
           {:handler (fn [response]
                       (let [{dbas :dbas/api
                              eden :eden/api} (cljs.reader/read-string response)]
                         (log/info "Found a valid service configuration file. Setting hosts to dbas: %s and eden: %s" dbas eden)
                         (lib/host-dbas! dbas)
                         (lib/host-eden! eden)))
            :error-handler #((log/warning "Remote service configuration could
                             not be found, although it is configured. Please
                             check the URL.")
                             (comcon/check-connectivity-of-hosts))}))))
