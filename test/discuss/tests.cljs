(ns discuss.tests
  (:require [cljs.test :refer-macros [deftest is run-all-tests]]
            [discuss.auth-test]
            [discuss.clipboard-test]
            [discuss.integration-test]))

(enable-console-print!)

(defn ^:export run []
  (run-all-tests #"discuss.*-test"))