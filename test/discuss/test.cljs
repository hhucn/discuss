(ns ^:figwheel-always discuss.test
  (:require [cljs.test :refer-macros [deftest is run-all-tests]]
            [discuss.auth-test]))

(enable-console-print!)

(defn ^:export run []
  (print "\n\n##### Running tests ######")
  (run-all-tests #"discuss.*-test")
  (println "##### End Running tests ######\n\n"))