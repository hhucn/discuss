(ns discuss.communication.specs
  (:require [cljs.spec.alpha :as s]
            [miner.strgen :as sg]))

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
