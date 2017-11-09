(ns discuss.specs
  (:require [cljs.spec.alpha :as s]
            [discuss.translations]))

(s/def ::options (s/or :option keyword?
                       :option nil?))

(s/fdef discuss.translations/translate
        :args (s/cat :group keyword?
                     :key keyword?
                     :option (s/? ::options))
        :ret string?)

;; -----------------------------------------------------------------------------
;; Define external data-structures

(s/def ::no-slash (s/and string? #(not (re-find #"/" %)) #(pos? (count %))))
(s/def ::author string?)
(s/def ::content string?)
(s/def ::aggregate-id ::no-slash)
(s/def ::entity-id ::no-slash)
(s/def ::version pos-int?)
(s/def ::created (s/or :nil nil? :timestamp string?)) ;; timestamp
(s/def ::ancestor-aggregate-id ::no-slash)
(s/def ::ancestor-entity-id ::no-slash)
(s/def ::ancestor-version ::version)
(s/def ::statement
  (s/keys :req-un [::author ::content
                   ::aggregate-id ::entity-id ::version
                   ::created]
          :opt-un [::ancestor-aggregate-id ::ancestor-entity-id ::ancestor-version]))
;; (s/exercise ::statement)


(s/def ::from-aggregate-id ::no-slash)
(s/def ::from-entity-id ::no-slash)
(s/def ::from-version ::version)
(s/def ::type keyword?)
(s/def ::to-aggregate-id ::no-slash)
(s/def ::to-entity-id ::no-slash)
(s/def ::to-version ::version)

(s/def ::link
  (s/keys :req-un [::author ::type
                   ::from-aggregate-id ::from-entity-id ::from-version
                   ::to-aggregate-id ::to-entity-id
                   ::aggregate-id ::entity-id
                   ::created]
          :opt-un [::to-version]))
;; (s/exercise ::link)

(s/def ::origin
  (s/keys :req-un [::author ::content ::aggregate-id ::version ::entity-id]
          :opt-un [::created]))
;; (s/exercise ::origin)

(comment
  (require '[clojure.spec.test.alpha :as stest]))
