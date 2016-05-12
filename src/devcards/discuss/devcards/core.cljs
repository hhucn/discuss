(ns discuss.devcards.core
  (:require [devcards.core :as dc :refer-macros [defcard deftest]]
            [cljs.test :refer-macros [testing is are]]
            [om.dom :as dom]
            [discuss.core :as core]
            [discuss.communication :as com]
            [discuss.debug :as debug]
            [discuss.extensions]
            [discuss.find :as find]
            [discuss.integration :as integration]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.views :as views]))

(enable-console-print!)

(defcard main
         (dc/om-root views/main-view)
         lib/app-state
         {:history true})

#_(defcard user-information
           ""
           (dom/div nil
                    (dom/h6 nil "User")
                    (dom/pre nil (lib/get-nickname))
                    (dom/h6 nil "Auth Token")
                    (dom/pre nil (lib/get-token))
                    (dom/h6 nil "CSRF Token")
                    (dom/pre nil (lib/get-csrf))
                    ))

(defcard control-buttons
         ""
         (dom/div nil
                  (dom/button #js {:className "btn btn-info"
                                   :onClick   core/main}
                              (dom/i #js {:className "fa fa-fort-awesome"}))
                  (debug/control-buttons lib/app-state)))

#_(defcard debug
           (dc/om-root debug/debug-view)
           lib/app-state)

#_(defcard global-state
           @lib/app-state)

(defcard find-statement
           "Query database to find stataments."
           (dc/om-root find/view))

(deftest find-tests
         "Testing the small search engine in `discuss.find`"
         (testing "fn statement, sending request and counting results"
           (find/statement "are")
           (is (= 4 (count (find/prepare-search-results))))))

(deftest integration-test
         "Testing `discuss.integration`"
         (let [doms-raw (.getElementsByTagName js/document "*")]
           (testing "fn get-parent"
             (is (= "test-get-parent" (.-id (integration/get-parent doms-raw "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"))))
             (is (= "figwheel-heads-up-content-area" (.-id (integration/get-parent doms-raw "")))))))

(deftest lib-test
         (testing "cljs to json conversion stuff"
           (let [json "{\"distance_name\": \"Levensthein\", \"values\": {\"00020_020\": \"Foobaraaaaaaaaaaaaaa.\", \"00033_013\": \"This is the only park in our city.\"}}"]
             (is (= {:distance_name "Levensthein", :values {:00020_020 "Foobaraaaaaaaaaaaaaa.", :00033_013 "This is the only park in our city."}} (lib/json->clj json))))))

#_(deftest communication-test
           "Testing `discuss.communication`")