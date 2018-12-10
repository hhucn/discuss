(ns discuss.eden.specs
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as string]))

(s/def ::no-slash (s/and string? #(not (re-find #"/" %)) #(pos? (count %))))
(s/def ::non-empty-string (s/and string? #((complement string/blank?) %)))

(s/def ::dgep-native boolean?)
(s/def ::name ::non-empty-string)
(s/def ::id pos-int?)
(s/def ::author (s/keys :req-un [::dgep-native ::name ::id]))

(s/def ::text ::non-empty-string)
(s/def ::created (s/or :nil nil? :timestamp string?)) ;; timestamp
(s/def ::content
  (s/keys :req-un [::text ::created ::author]))

(s/def ::aggregate-id ::no-slash)
(s/def ::entity-id ::no-slash)
(s/def ::version pos-int?)
(s/def ::identifier
  (s/keys :req-un [::aggregate-id ::entity-id ::version]))

(s/def ::predecessors (s/coll-of ::identifier))
(s/def ::delete-flag boolean?)
(s/def ::statement
  (s/keys :req-un [::content
                   ::identifier ::predecessors
                   ::delete-flag]))

;; (s/exercise ::statement)

(s/def ::type keyword?)
(s/def ::source ::identifier)
(s/def ::destination ::identifier)
(s/def ::link
  (s/keys :req-un [::type
                   ::source ::destination
                   ::identifier ::delete-flag]
          :opt-un [::created ::author]))

(s/def ::premise ::statement)
(s/def ::conclusion ::statement)
(s/def ::argument (s/keys :req-un [::premise ::conclusion ::link]))
