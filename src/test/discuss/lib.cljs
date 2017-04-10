(ns discuss.test.lib
  (:require [cljs.pprint :as pprint]
            [cljs.test :refer-macros [is]]))

(defn- summarize-results' [spec-check]
  (doall (map #(-> % :clojure.test.check/ret pprint/pprint) spec-check)))

(defn check' [spec-check]
  (summarize-results' spec-check)
  (is (nil? (-> spec-check first :failure))))
