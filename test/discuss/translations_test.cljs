(ns discuss.translations-test
  (:require [cljs.test :refer-macros [deftest is are testing]]
            [discuss.translations :as t]))

(deftest translate
  (testing "Requesting a translation should always return a string, even when it
  is the empty string."
    (is (= " " (last (t/translate :foo :bar :space))))
    (is (= "" (t/translate :definitely/not-a-key-in-my-dict :bar)))))
