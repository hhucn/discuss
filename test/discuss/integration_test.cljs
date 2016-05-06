(ns discuss.integration-test
  (:require [cljs.test :refer-macros [deftest is run-tests]]
            #_[discuss.integration :as integration]))

(def foo 42)

(deftest success
         (is (= 1 2)))

#_(deftest test-convert-reference
  "Inclusion of previously used references." )