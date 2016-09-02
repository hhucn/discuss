(ns discuss.tests
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]))

(clojure.test.check.generators/vector)
(gen/sample (gen/int))

(require '[clojure.test.check :as tc])
(require '[clojure.test.check.generators :as gen])
(require '[clojure.test.check.properties :as prop])

(gen/sample (clojure.test.check.generators/int))

