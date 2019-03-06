(ns discuss.test-runner
  (:require [cljs.test]
            [cljs-test-display.core]
            [figwheel.main.testing :refer-macros [run-tests-async]]
            [discuss.translations-test]
            [discuss.utils.common-test]
            [discuss.references.lib-test]
            [discuss.communication.lib-test])
  (:require-macros [cljs.test]))

(defn test-run []
  (cljs.test/run-tests
   (cljs-test-display.core/init! "app-tests")
   'discuss.translations-test
   'discuss.utils.common-test
   'discuss.references.lib-test
   'discuss.communication.lib-test))

#_(test-run)


(defn -main [& args]
  ;; this needs to be the last statement in the main function so that it can
  ;; return the value `[:figwheel.main.async-result/wait 10000]`
  (run-tests-async 10000))
