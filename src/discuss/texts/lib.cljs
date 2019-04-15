(ns discuss.texts.lib
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as string]
            [discuss.translations :refer [translate] :rename {translate t}]))

(defn join-with-and
  "Join collection of strings with `and`."
  [col]
  (string/join (str " <i>" (t :common :and) "</i> ") col))
(s/fdef join-with-and
  :args (s/cat :col (s/coll-of string?))
  :ret string?)

