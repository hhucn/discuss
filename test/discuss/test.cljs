(ns discuss.test
  (:require [cljs.test :refer-macros [deftest is run-all-tests]]))

(enable-console-print!)

(deftest do-i-work
  (is (= 1 2)))

(deftest fail
  (is (= "foo" "bar")))

(defn ^:export run []
  (run-all-tests #"discuss.test"))