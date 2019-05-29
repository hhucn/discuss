(ns discuss.texts.references
  (:require [cljs.spec.alpha :as s]
            [goog.string :refer [format]]
            [goog.string.format]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.texts.lib :as textlib]))

(defn- undercut-text
  "Create string which represents an undercut."
  [nickname premise attacks]
  (format "Laut %s kann man die Aussage, dass \"%s\", nicht damit begründen, dass \"%s\", weil %s."
          nickname
          (textlib/highlight-conclusion (:conclusion attacks))
          (textlib/highlight-premise (:premise attacks))
          (textlib/highlight-undercut premise)))

(defn- argument-text
  "Given a conclusion and premise, construct a string representing an argument."
  [nickname conclusion premise]
  (format "%s %s %s, %s %s."
          nickname
          (t :common :argue/that)
          (textlib/highlight-conclusion conclusion)
          (t :common :because)
          (textlib/highlight-premise premise)))

(defn reference-usage-intro
  "Construct reference usage intro. If attacks are provided, the target attack is
  an argument itself, which makes the whole attack an undercut."
  [nickname conclusion premise attacks]
  (if (seq attacks)
    (undercut-text nickname premise attacks)
    (argument-text nickname conclusion premise)))

(s/def ::nickname string?)
(s/def ::conclusion string?)
(s/def ::premise string?)
(s/def ::attacks (s/or :empty nil? :undercut map?))

(s/fdef reference-usage-intro
  :args (s/cat :nickname ::nickname :conclusion ::conclusion :premise ::premise :attacks ::attacks)
  :ret string?)

(s/fdef argument-text
  :args (s/cat :nickname ::nickname :conclusion ::conclusion :premise ::premise)
  :ret string?)

(s/fdef undercut-text
  :args (s/cat :nickname ::nickname :conclusion ::conclusion :premise ::premise :attacks ::attacks)
  :ret string?)
