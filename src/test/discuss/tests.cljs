(ns discuss.tests
  "Test runner to execute all tests."
  (:require [doo.runner :refer-macros [doo-tests]]
            [discuss.components.clipboard-test]
            [discuss.references.lib-test]
            [discuss.utils.common-test]))

(enable-console-print!)

(doo-tests 'discuss.components.clipboard-test
           'discuss.references.lib-test
           'discuss.utils.common-test)