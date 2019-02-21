(ns discuss.history.discussion
  (:require [discuss.utils.common :as lib]
            [cljs.spec.alpha :as s]
            [discuss.utils.logging :as log]))

(defn get-discussion-urls
  "Return URLs of clicked items."
  []
  (lib/load-from-app-state :history/discussion-steps))
(s/fdef get-discussion-urls
  :ret (s/coll-of string?))

(defn get-last-discussion-url
  "Return last URL which was used in the discussion. Can be used to post new
  statements to this route, because this creates a new statement."
  []
  (last (get-discussion-urls)))
(s/fdef get-last-discussion-url
  :ret string?)

(defn save-discussion-url!
  "Takes a URL from the clicked item and stores it in the app-state."
  [url]
  (log/info "Saving last discussion url: %s" url)
  (lib/store-to-app-state! 'history/discussion-steps (conj (get-discussion-urls) url)))
(s/fdef save-discussion-url!
  :args (s/cat :url string?))

(defn save-discussion-urls!
  "Takes a URL from the clicked item and stores it in the app-state."
  [col-of-urls]
  (lib/store-to-app-state! 'history/discussion-steps col-of-urls))
(s/fdef save-discussion-urls!
  :args (s/cat :col-of-urls (s/coll-of string?)))
