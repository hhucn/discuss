(ns discuss.communication.lib
  "Helper-functions for the communication component."
  (:require [clojure.string :as str]
            [cljs.spec.alpha :as s]
            [ajax.core :refer [GET POST]]
            [om.next :as om]
            [miner.strgen :as sg]
            [discuss.config :as config]
            [discuss.utils.common :as lib]
            [discuss.parser :as parser]
            [discuss.utils.logging :as log]))

;;;; Auxiliary functions
(defn make-url
  "Prefix url with host."
  [url]
  (str (:host config/api) url))

(defn token-header
  "Return token header for ajax request if user is logged in."
  []
  (when (lib/logged-in?)
    {"X-Authentication" (lib/clj->json {:type "user" :nickname (lib/get-nickname) :token (lib/get-token)})}))


;;;; Handlers
(defn error-handler
  "Generic error handler for ajax requests."
  [{:keys [status status-text]}]
  (lib/log (str "I feel a disturbance in the Force... " status " " status-text))
  (lib/loading? false))

(defn index-handler
  [response]
  (let [{:keys [items bubbles]} (lib/process-response response)]
    (lib/change-to-next-view!)
    (om/transact! parser/reconciler `[(discussion/items {:value ~items})
                                      (discussion/bubbles {:value ~bubbles})])))

(defn process-discussion-step
  "Handler to process the response from a discussion step when an item is clicked."
  [response]
  (let [{:keys [bubbles attitudes items attacks]} (lib/process-response response)
        update-items (cond
                       (seq items) items
                       (seq attacks) (vals attacks)
                       (seq attitudes) (vals attitudes)
                       :default [])]
    (om/transact! parser/reconciler `[(discussion/bubbles {:value ~bubbles})
                                      (discussion/items {:value ~update-items})])))

(defn ajax-get
  "Make ajax call to dialog based argumentation system."
  ([url headers handler params]
   (lib/last-api! url)
   (log/debug "Request to:" (make-url url))
   (GET (make-url url)
        {:handler       handler
         :headers       (merge (token-header) headers)
         :params        params
         :error-handler error-handler}))
  ([url headers handler] (ajax-get url headers handler nil))
  ([url headers] (ajax-get url headers process-discussion-step))
  ([url] (ajax-get url (token-header))))

(defn ajax-get-and-change-view
  "Make ajax call to jump right into the discussion and change to discussion
  view."
  ([url view handler]
   (lib/next-view! view)
   (ajax-get url {} handler))
  ([url view]
   (ajax-get-and-change-view url view process-discussion-step)))


(defn jump-to-argument
  "Jump directly into the discussion to let the user argue about the given argument.

   ** TODO: Update route **"
  [slug arg-id]
  (let [url (str/join "/" ["api" slug "jump" arg-id])]
    (ajax-get-and-change-view url :discussion)))

(defn init!
  "Request initial data from API."
  []
  (let [url (:init config/api)]
    (ajax-get-and-change-view url :default index-handler)))


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

(s/fdef process-discussion-step
        :args (s/cat :response ::response))
