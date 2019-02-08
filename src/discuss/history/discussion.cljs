(ns discuss.history.discussion
  (:require [discuss.utils.common :as lib]
            [cljs.spec.alpha :as s]))

(defn get-discussion-urls
  "Return URLs of clicked items."
  []
  (lib/load-from-app-state :history/discussion-steps))
(s/fdef get-discussion-urls
  :ret (s/coll-of string?))

(defn save-discussion-url!
  "Takes a URL from the clicked item and stores it in the app-state."
  [url]
  (lib/store-to-app-state! 'history/discussion-steps (conj (get-discussion-urls) url)))
(s/fdef save-discussion-url!
  :args (s/cat :url string?))

(defn save-discussion-urls!
  "Takes a URL from the clicked item and stores it in the app-state."
  [col-of-urls]
  (lib/store-to-app-state! 'history/discussion-steps col-of-urls))
(s/fdef save-discussion-urls!
  :args (s/cat :col-of-urls (s/coll-of string?)))
