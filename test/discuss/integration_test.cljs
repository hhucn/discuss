(ns discuss.integration-test
  (:require [cljs.test :refer-macros [deftest is run-tests]]))

(deftest success
         (is (= 1 1)))