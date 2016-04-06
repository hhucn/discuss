(ns discuss.test
  (:require [cljs.test :refer-macros [deftest is testing run-tests run-all-tests]]))

(enable-console-print!)

(println "### Tests ###")

(defn ^:export run []
  (run-all-tests #"discuss.*-test"))