(ns discuss.devcards.core
  (:require [devcards.core :as dc :refer-macros [defcard deftest]]
            [cljs.test :refer-macros [testing is are]]
            [om.dom :as dom]
            [discuss.core :as core]
            [discuss.communication :as com]
            [discuss.debug :as debug]
            [discuss.extensions]
            [discuss.integration :as integration]
            [discuss.lib :as lib]
            [discuss.lib.views :as vlib]
            [discuss.views :as views]))

(enable-console-print!)

(defcard main
         "#### Main discuss Component"
         (dc/om-root views/main-view)
         lib/app-state
         {:history true})

(defcard user-information
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

(deftest integration-test
         "Testing `discuss.integration`"
         (let [doms-raw (.getElementsByTagName js/document "*")]
           (testing "fn get-parent"
             (is (integration/get-parent doms-raw "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"))
             ;(is (= "PRE" (.-nodeName (integration/get-parent doms-raw "foo bar baz lorem ipsum"))))
             (is (= "figwheel-heads-up-content-area" (.-id (integration/get-parent doms-raw "")))))
           (testing "fn convert-reference"
             (is true))))

(deftest communication-test
         "Testing `discuss.communication`"
         (testing "fn token-header"
           (is (= {"X-CSRF-Token" nil} (com/token-header)))))

(defcard integration-stuff
         (.-id (integration/get-parent (.getElementsByTagName js/document "*") "")))