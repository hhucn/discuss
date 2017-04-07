(ns discuss.test.lib
  (:require [cljs.pprint :as pprint]
            [cljs.test :refer-macros [is]]))

(defn- summarize-results' [spec-check]
  (map #(-> % :clojure.test.check/ret pprint/pprint) spec-check))

(defn check' [spec-check]
  (is (nil? (-> spec-check first :failure)) (summarize-results' spec-check)))
