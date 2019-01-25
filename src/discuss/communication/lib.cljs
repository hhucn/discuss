(ns discuss.communication.lib
  "Helper-functions for the communication component."
  (:require [clojure.string :as str]
            [cljs.spec.alpha :as s]
            [ajax.core :refer [GET POST]]
            [om.next :as om]
            [goog.string :refer [format]]
            [goog.string.format]
            [miner.strgen :as sg]
            [discuss.config :as config]
            [discuss.utils.common :as lib]
            [discuss.parser :as parser]
            [discuss.utils.logging :as log]
            [discuss.translations :refer [translate] :rename {translate t}]))

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
                       :default [])]
    (om/transact! parser/reconciler `[(discussion/bubbles {:value ~bubbles})
                                      (discussion/items {:value ~update-items})
                                      (discussion/add-step {:value ~add-step})])))


;;;; Handlers
(defn error-handler
  "Generic error handler for ajax requests."
  [{:keys [status status-text response]}]
  (log/error (str "Request failed with status code " status " (" status-text ")"))
  (lib/error! (str/join (str " " (t :common :and) " ") (map :name (:errors response)))))

(defn index-handler
  [response]
  (lib/change-to-next-view!)
  (process-and-set-items-and-bubbles response))

(defn ajax-get
  "Make ajax call to dialog based argumentation system."
  ([url headers handler params]
   (lib/last-api! url)
   (log/debug "GET Request to:" (make-url url))
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
    (ajax-get-and-change-view with-arg :discussion)))

(defn init!
  "Request initial data from API. Optionally provide a slug to change the
  discussion."
  ([]
   (init! (:init config/api)))
  ([slug]
   (log/fine (format "Initializing discussion: %s" (:init config/api)))
   (ajax-get-and-change-view slug :default index-handler)))

(s/fdef init!
  :args (s/? (s/cat :slug string?)))


;; -----------------------------------------------------------------------------
;; Add compatibility to D-BAS' new API

(s/def ::text (let [re #"[^<>]*"]
                (s/spec (s/and string? #(re-matches re %))
                        :gen #(sg/string-generator re))))
(s/def ::texts (s/coll-of ::text))

(s/def ::html string?)
(s/def ::htmls (s/coll-of ::html))

(s/def ::url (s/or :has-url string? :no-url nil?))

(s/def ::bubble (s/keys :req-un [::html ::text ::url]))
(s/def ::bubbles (s/coll-of ::bubble))

(s/def ::item (s/and (s/keys :req-un [::htmls ::texts ::url])
                     #(= (count (:htmls %)) (count (:texts %)))))
(s/def ::items (s/coll-of ::item))

(s/def ::agree ::item)
(s/def ::disagree ::item)
(s/def ::dontknow ::item)
(s/def ::attitudes (s/keys :req-un [::agree ::disagree ::dontknow]))

(s/def ::step_back ::item)
(s/def ::undermine ::item)
(s/def ::undercut ::item)
(s/def ::rebut ::item)

(s/def ::attacks (s/keys :req-un [::step_back]
                         :opt-un [::undermine ::undercut ::rebut]))

(s/def ::response (s/keys :req-un [::bubbles]
                          :opt-un [::attitudes ::attacks ::items]))

(s/fdef process-and-set-items-and-bubbles
        :args (s/cat :response ::response))

(s/fdef login-or-add-item
  :args (s/cat :logged-in? boolean?)
  :ret ::item)
