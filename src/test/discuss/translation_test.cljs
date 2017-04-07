(ns discuss.translations-test
  (:require [cljs.test :refer-macros [deftest is are testing]]
            [clojure.spec.test :as stest]
            [discuss.translations :as t]
            [discuss.test.lib :as tlib]))

(deftest translate
  (testing "Requesting a translation should always return a string, even when it
  is the empty string."
    (tlib/check' (stest/check 'discuss.translations/translate))
    (is (= " " (last (t/translate :foo :bar :space))))
    (is (= "" (t/translate :definitely/not-a-key-in-my-dict :bar)))))
