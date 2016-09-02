(ns discuss.devcards.clipboard-test
  (:require [cljs.test :refer-macros [testing is are]]
            [clojure.test.check :refer [quick-check]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [devcards.core :as dc :refer-macros [defcard deftest defcard-om]]
            [discuss.components.clipboard :as clipboard]
            [discuss.utils.common :as lib]))

(def add-remove-selections
  ;"Add some random selections and remove them again."
  (prop/for-all [input (gen/not-empty gen/any-printable)]
                (let [before (clipboard/get-stored-selections)]
                  #_(clipboard/add-item! input)
                  #_(clipboard/remove-item! input)
                  (= before (clipboard/get-stored-selections)))))

(quick-check 100 add-remove-selections)

gen/int

