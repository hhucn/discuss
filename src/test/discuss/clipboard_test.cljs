(ns discuss.clipboard-test
  (:require                                                 ;[cljs.test :refer-macros [testing is are]]
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
  (prop/for-all [input gen/any-printable]
                (let [before (clipboard/get-stored-selections)]
                  #_(clipboard/add-item! input)
                  #_(clipboard/remove-item! input)
                  (= before (clipboard/get-stored-selections)))))
(clojure.test.check/quick-check 1 add-remove-selections)



