(ns discuss.references.lib-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer-macros [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [cljs.spec :as s]
            [discuss.references.lib :as rlib]
            [discuss.utils.common :as lib]))

(deftest already-highlighted?
  (testing "Given a reference, only highlight it in text if this has not been done yet."
    (let [highlighted (rlib/get-highlighted)
          input "\"Wenn man ein 0:2 kassiert, dann ist ein 1:1 nicht mehr möglich\" - Satz des Pythagoras"]
      (is (set? highlighted))
      (rlib/highlight! input)
      (is (rlib/highlighted? input)))))