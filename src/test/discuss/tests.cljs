(ns discuss.tests
  (:require [cljs.test :refer-macros [is are deftest run-tests]]
            [doo.runner :refer-macros [doo-tests]]
            [discuss.devcards.lib]))

(doo-tests 'discuss.devcards.lib)

