(ns discuss.clipboard-test
  (:require                                                 ;[cljs.test :refer-macros [testing is are]]
    [cljs.test :refer-macros [deftest is]]
    [clojure.test.check :as tc]
    [clojure.test.check.clojure-test :refer-macros [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop :include-macros true]
    ;[devcards.core :as dc :refer-macros [defcard deftest defcard-om]]
    [discuss.components.clipboard :as clipboard]
    ;       [discuss.utils.common :as lib]
    ))

(defspec add-remove-selections
         ;"Add some random selections and remove them again."
         100
         (prop/for-all [input gen/any-printable]
                       (let [before (clipboard/get-stored-selections)]
                         (clipboard/add-item! input)
                         (clipboard/remove-item! input)
                         (= before (clipboard/get-stored-selections)))))

(defspec first-element-is-min-after-sorting                 ;; the name of the test
         100                                                ;; the number of iterations for test.check to test
         (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
                       (= (apply min v)
                          (first (sort v)))))