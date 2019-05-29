(ns discuss.texts.lib
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as string]
            [goog.string :refer [format]]
            [goog.string.format]
            [discuss.translations :refer [translate] :rename {translate t}]))

(defn join-with-and
  "Join collection of strings with `and`."
  [col]
  (string/join (str " <i>" (t :common :and) "</i> ") col))
(s/fdef join-with-and
  :args (s/cat :col (s/coll-of string?))
  :ret string?)


;; -----------------------------------------------------------------------------
;; Highlight strings with html classes

(defn highlight-premise [premise]
  (format "<span class=\"text-info\">%s</span>" premise))

(defn highlight-conclusion [premise]
  (format "<span class=\"text-warning\">%s</span>" premise))

(defn highlight-undercut [premise]
  (format "<span class=\"text-success\">%s</span>" premise))


