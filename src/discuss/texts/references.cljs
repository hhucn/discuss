(ns discuss.texts.references
  (:require [cljs.spec.alpha :as s]
            [goog.string :refer [format]]
            [goog.string.format]
            [discuss.translations :refer [translate] :rename {translate t}]))

(defn reference-usage-intro
  "Construct reference usage intro."
  [nickname conclusion premise]
  (format "%s %s %s, %s %s"
          nickname (t :common :argue/that) conclusion (t :common :because) premise))
(s/fdef reference-usage-intro
  :args (s/cat :nickname string? :conclusion string? :premise string?)
  :ret string?)
