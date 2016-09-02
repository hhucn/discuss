(ns discuss.tests
  "Test runner to execute all tests."
  (:require [doo.runner :refer-macros [doo-tests]]
            [discuss.utils.common-test]
            [discuss.components.clipboard-test]))

(enable-console-print!)

(doo-tests 'discuss.components.clipboard-test
           'discuss.utils.common-test)