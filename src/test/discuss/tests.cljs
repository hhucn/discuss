(ns discuss.tests
  "Test runner to execute all tests."
  (:require [doo.runner :refer-macros [doo-tests]]
            [discuss.clipboard-test]))

(enable-console-print!)

(doo-tests 'discuss.clipboard-test)

