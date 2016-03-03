(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.views :as views]))

(enable-console-print!)

(def app-state
  (atom {:title "discuss"
         :intro "The current discussion is about"
         :discussion
                {:heading {:intro  "What is the initial position you are interested in?"
                           :outro  ""
                           :bridge ""}}

         :issue
                {:uid  1
                 :date "12:41 PM, 24. Feb. 2016"
                 :info "Your familiy argues about whether to buy a cat or dog as pet. Now your opinion matters!",
                 :slug "cat-or-dog",
                 :title "Cat or Dog",
                 :arg_count 99}

         :items
                [{:id       "item_1"
                  :title    "We should get a cat"
                  :url      "api/cat-or-dog/attitude/1"
                  :attitude "start"}
                 {:id       "item_2"
                  :title    "We should get a dog"
                  :url      "api/cat-or-dog/attitude/2"
                  :attitude "start"}
                 {:id       "item_3"
                  :title    "We could get both, a cat and a dog"
                  :url      "api/cat-or-dog/attitude/3"
                  :attitude "start"}]}))


;; Register
(om/root views/main-view app-state
         {:target (. js/document (getElementById "discuss-main"))})

(om/root views/clipboard-view app-state
         {:target (. js/document (getElementById "discuss-clipboard"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
