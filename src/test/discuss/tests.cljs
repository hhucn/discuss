(ns discuss.tests
  (:require [cljs.test :refer-macros [is are deftest run-tests]]
            [doo.runner :refer-macros [doo-tests]]
            [discuss.clipboard-test]))

(doo-tests 'discuss.clipboard-test)

