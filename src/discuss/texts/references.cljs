(ns discuss.texts.references
  (:require [cljs.spec.alpha :as s]
            [goog.string :refer [format]]
            [goog.string.format]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.texts.lib :as textlib]))

(defn reference-usage-intro
  "Construct reference usage intro. If attacks are provided, the target attack is
  an argument itself, which makes the whole attack an undercut."
  [nickname conclusion premise attacks]
  (if (seq attacks)
    (format "Laut %s kann man die Aussage, dass \"%s\", nicht damit begründen, dass \"%s\", weil %s."
            nickname
            (textlib/highlight-conclusion (:conclusion attacks))
            (textlib/highlight-premise (:premise attacks))
            (textlib/highlight-undercut premise))
    (format "%s %s %s, %s %s."
            nickname
            (t :common :argue/that)
            (textlib/highlight-conclusion conclusion)
            (t :common :because)
            (textlib/highlight-premise premise))))

(s/fdef reference-usage-intro
  :args (s/cat :nickname string? :conclusion string? :premise string? :attacks (s/or :empty nil? :undercut map?))
  :ret string?)
