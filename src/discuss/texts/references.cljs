(ns discuss.texts.references
  (:require [cljs.spec.alpha :as s]
            [goog.string :refer [format]]
            [goog.string.format]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.texts.lib :as textlib]))

(defn- undercut-text
  "Create string which represents an undercut."
  [nickname premise attacks]
  (format (t :undercut :text)
          nickname
          (textlib/highlight-conclusion (:conclusion attacks))
          (textlib/highlight-premise (:premise attacks))
          (textlib/highlight-undercut premise)))

(defn- argument-text
  "Given a conclusion and premise, construct a string representing an argument."
  [nickname conclusion premise is_supportive]
  (let [supportive-translation (if is_supportive :argue/that :argue/con)]
    (format "%s %s %s, %s %s."
            nickname
            (t :common supportive-translation)
            (textlib/highlight-conclusion conclusion)
            (t :common :because)
            (textlib/highlight-premise premise))))

(defn reference-usage-intro
  "Construct reference usage intro. If attacks are provided, the target attack is
  an argument itself, which makes the whole attack an undercut."
  [nickname conclusion premise attacks is_supportive]
  (if (seq attacks)
    (undercut-text nickname premise attacks)
    (argument-text nickname conclusion premise is_supportive)))

(s/def ::nickname string?)
(s/def ::conclusion string?)
(s/def ::premise string?)
(s/def ::attacks (s/or :empty nil? :undercut map?))
(s/def ::undercut string?)

(s/fdef reference-usage-intro
  :args (s/cat :nickname ::nickname :conclusion ::conclusion :premise ::premise :attacks ::attacks :is_supportive string?)
  :ret string?)

(s/fdef argument-text
  :args (s/cat :nickname ::nickname :conclusion ::conclusion :premise ::premise)
  :ret string?)

(s/fdef undercut-text
  :args (s/cat :nickname ::nickname :conclusion ::conclusion :premise ::premise :attacks ::attacks)
  :ret string?)
