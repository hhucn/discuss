(ns discuss.test.lib
  (:require [cljs.pprint :as pprint]
            [cljs.test :refer-macros [is]]))

(defn- summarize-results' [spec-check]
  (doall (map #(-> %
                   (select-keys [:clojure.test.check/ret :sym])
                   vals
                   pprint/pprint) spec-check)))

(defn check' [spec-check]
  (summarize-results' spec-check)
  (is (nil? (-> spec-check first :failure))))
