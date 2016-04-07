(ns ^:figwheel-always discuss.auth-test
  (:require [cljs.test :refer-macros [deftest is]]))

;(deftest fail
;  (is (= 1 2)))

(deftest success
  (is (= 1 1)))