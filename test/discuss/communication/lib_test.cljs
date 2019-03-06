(ns discuss.communication.lib-test
  (:require [cljs.test :refer [deftest testing is are]]
            [discuss.communication.lib :as comlib]))

(def bubbles [{:type "user", :html "Foo bar baz.", :url "/cat-or-dog/justify/121/agree?history=attitude/121", :text "Foo bar baz."}
              {:type "status", :html "Now", :url nil, :text "Now"}
              {:type "user", :html "Foo bar baz because this is sparta.", :url nil, :text "Foo bar baz because this is sparta."}
              {:type "system", :html "Other participants do not have any counter-argument for that.", :url nil, :text "Other participants do not have any counter-argument for that."}
              {:type "info", :html "<i class=\"fa fa-trophy\" aria-hidden=\"true\"></i> Congratulation! <i class=\"fa fa-trophy\" aria-hidden=\"true\"></i> <br>The discussion ends here, because there is no other counter argument at the moment.Are you looking for things to do? <a  data-href=\"login\" data-toggle=\"modal\" data-target=\"#popup-login\" title=\"Login\">Login</a> and take a look at the <a id=\"discussionEndReview\">review section</a>. Alternatively you can restart the discussion with the button at the bottom left.", :url nil, :text " Congratulation!  The discussion ends here, because there is no other counter argument at the moment.Are you looking for things to do? Login and take a look at the review section. Alternatively you can restart the discussion with the button at the bottom left."}])

(deftest replace-congratulation-bubble-text-test
  (testing "There should be no trophy after the conversation."
    (is (nil? (re-find #"fa-trophy" (:html (comlib/replace-congratulation-bubble-text (last bubbles))))))
    (is (nil? (re-find #"fa-trophy" (:html (comlib/replace-congratulation-bubble-text (first bubbles))))))))

(deftest replace-congratulation-bubble-test
  (testing "Takes a collection of bubbles and replaces the
  congratulation-bubble."
    (let [with-converted-congratulation-bubble (comlib/replace-congratulation-bubble bubbles)]
      (is (not= bubbles with-converted-congratulation-bubble))
      (is (= (count bubbles) (count with-converted-congratulation-bubble))))))
