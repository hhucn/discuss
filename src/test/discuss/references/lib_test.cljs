(ns discuss.references.lib-test
  (:require [cljs.test :refer-macros [deftest is are testing]]
            [clojure.spec.test.alpha :as stest]
            [discuss.references.lib :as rlib]
            [discuss.references.specs]
            [discuss.test.lib :as tlib]))

(deftest already-highlighted?
  (testing "Given a reference, only highlight it in text if this has not been done yet."
    (let [highlighted (rlib/get-highlighted)
          input "\"Wenn man ein 0:2 kassiert, dann ist ein 1:1 nicht mehr möglich\" - Satz des Pythagoras"]
      (is (set? highlighted))
      (rlib/highlight! input)
      (is (rlib/highlighted? input)))))

(deftest split-at-string
  (testing "Split the input string in two parts at the replacement-string."
    (tlib/check' (stest/check `discuss.references.lib/split-at-string))
    (are [x y] (= x y)
      [""]          (rlib/split-at-string "" "")
      [""]          (rlib/split-at-string "" "abc")
      ["abc"]       (rlib/split-at-string "abc" "")
      []            (rlib/split-at-string "1" "1")
      ["abc"]       (rlib/split-at-string "abc" "def")
      ["" "bar"]    (rlib/split-at-string "foobar" "foo")
      ["" "foo"]    (rlib/split-at-string "foofoo" "foo")
      ["foo" ""]    (rlib/split-at-string "foobar" "bar")
      ["a" "c"]     (rlib/split-at-string "abc" "b")
      ["bar" "baz"] (rlib/split-at-string "barfoo?baz" "foo?"))))
