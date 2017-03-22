(ns discuss.components.clipboard-test
  (:require [cljs.test :refer-macros [deftest is]]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer-macros [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [discuss.components.clipboard :as clipboard]))

(defspec add-remove-selections
  ;; "Add some random selections and remove them again."
  100
  (prop/for-all [input gen/any-printable]
                (let [before (clipboard/get-stored-selections)]
                  (clipboard/add-item! input)
                  (clipboard/remove-item! input)
                  (= before (clipboard/get-stored-selections)))))
