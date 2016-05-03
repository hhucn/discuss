(ns discuss.clipboard-test
  (:require [cljs.test :refer-macros [deftest is run-tests]]))

(enable-console-print!)

(deftest failing
  (is (= 1 2)))

(deftest successing
  (is (= 1 1)))

;(run-tests)