(ns ^:figwheel-load discuss.auth-test
  (:require [cljs.test :refer-macros [deftest is run-tests]]))

(enable-console-print!)

(deftest fail
  (is (= 1 2)))

(deftest success
  (is (= 1 1)))
