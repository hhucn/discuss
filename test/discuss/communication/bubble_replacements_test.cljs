(ns discuss.communication.bubble-replacements-test
  (:require [cljs.test :refer [deftest testing is are]]
            [discuss.communication.bubble-replacements :as breps]))

(def bubbles [{:type "user", :html "Foo bar baz.", :url "/cat-or-dog/justify/121/agree?history=attitude/121", :text "Foo bar baz."}
              {:type "status", :html "Now", :url nil, :text "Now"}
              {:type "user", :html "Foo bar baz because this is sparta.", :url nil, :text "Foo bar baz because this is sparta."}
              {:type "system", :html "Other participants do not have any counter-argument for that.", :url nil, :text "Other participants do not have any counter-argument for that."}])

(def bubbles-with-trophy
  (conj bubbles
        {:type "info", :html "<i class=\"fa fa-trophy\" aria-hidden=\"true\"></i> Congratulation! <i class=\"fa fa-trophy\" aria-hidden=\"true\"></i> <br>The discussion ends here, because there is no other counter argument at the moment.Are you looking for things to do? <a  data-href=\"login\" data-toggle=\"modal\" data-target=\"#popup-login\" title=\"Login\">Login</a> and take a look at the <a id=\"discussionEndReview\">review section</a>. Alternatively you can restart the discussion with the button at the bottom left.", :url nil, :text " Congratulation!  The discussion ends here, because there is no other counter argument at the moment.Are you looking for things to do? Login and take a look at the review section. Alternatively you can restart the discussion with the button at the bottom left."}))

(deftest replace-congratulation-bubble-text-test
  (testing "There should be no trophy after the conversion."
    (is (nil? (re-find #"fa-trophy" (:html (breps/replace-congratulation-bubble-text (last bubbles-with-trophy))))))
    (is (nil? (re-find #"fa-trophy" (:html (breps/replace-congratulation-bubble-text (first bubbles-with-trophy))))))))

(deftest replace-congratulation-bubble-test
  (testing "Takes a collection of bubbles and replaces the congratulation-bubble."
    (let [with-converted-congratulation-bubble (breps/replace-congratulation-bubbles bubbles-with-trophy)]
      (is (not= bubbles-with-trophy with-converted-congratulation-bubble))
      (is (= (count bubbles-with-trophy) (count with-converted-congratulation-bubble)))))

  (testing "If there is no trophy-bubble, nothing should have changed"
    (let [bubbles-after-fn (breps/replace-congratulation-bubbles bubbles)]
      (is (= bubbles bubbles-after-fn))
      (is (= (count bubbles) (count bubbles-after-fn))))))


;; -----------------------------------------------------------------------------
;; Test Replace Profile Link

(def bubbles-with-profile
  (conj bubbles
        {:type "system", :html "<a href=\"/user/26\" title=\"Konstanze\"><img class=\"img-circle\" src=\"https://secure.gravatar.com/avatar/fed0364ca72c4c1f0bd59d2ab09787c2?d=identicon&s=20\" style=\"padding-right: 0.3em\">Konstanze</a> <span class=\"triangle-content-text\">agrees that it would be no problem. But she does <span data-attitude=\"con\">not</span> believe that this is <span data-argumentation-type=\"argument\">a good argument for</span></span> <span data-argumentation-type=\"argument\">we could get both, a cat and a dog</span><span>. She thinks that</span> <span data-argumentation-type=\"attack\">a cat and a dog will generally not get along well and won't be best friends</span><span>.<br><br>What do you think about that?</span>", :url nil, :text "Konstanze agrees that it would be no problem. But she does not believe that this is a good argument for we could get both, a cat and a dog. She thinks that a cat and a dog will generally not get along well and won't be best friends.What do you think about that?"}))

(deftest replace-profile-link-test
  (testing "If there is a profile link in a bubble, replace it with an external link."
    (is (nil? (re-find #"\"/user/" (:html (breps/replace-profile-link (last bubbles-with-profile))))))))

(deftest replace-profile-link-bubbles-test
  (testing "Bubbles with profile links should be replaced with absolute links."
    (let [with-converted-profile-bubble (breps/replace-profile-link-bubbles bubbles-with-profile)]
      (is (not= bubbles-with-profile with-converted-profile-bubble))
      (is (= (count bubbles-with-profile) (count with-converted-profile-bubble)))))

  (testing "Bubbles with no profile links should not be touched."
    (let [bubbles-after-fn (breps/replace-profile-link-bubbles bubbles)]
      (is (= bubbles bubbles-after-fn))
      (is (= (count bubbles) (count bubbles-after-fn))))))
