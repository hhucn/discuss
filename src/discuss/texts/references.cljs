(ns discuss.texts.references
  (:require [cljs.spec.alpha :as s]
            [goog.string :refer [format]]
            [goog.string.format]
            [discuss.texts.lib :as textlib]
            [discuss.translations :refer [translate] :rename {translate t}]))

(defn reference-usage-intro
  "Construct reference usage intro."
  [nickname conclusions premises]
  (let [joined-conclusions (textlib/join-with-and conclusions)
        joined-premises    (textlib/join-with-and premises)]
    (format "%s %s %s, %s %s" nickname (t :common :argue/that) joined-conclusions (t :common :because) joined-premises)))
(s/fdef reference-usage-intro
  :args (s/cat :nickname string? :conclusions (s/coll-of string?) :premises (s/coll-of string?))
  :ret string?)
