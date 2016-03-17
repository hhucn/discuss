(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [discuss.communication :as com]
            [discuss.debug :as debug]
            [discuss.extensions]
            [discuss.integration :as integration]
            [discuss.lib :as lib]
            [discuss.views :as views]))

;; Initialization
(com/init!)

;; Register
(om/root views/main-view lib/discussion-state
         {:target (.getElementById js/document "discuss-main")})

(om/root debug/debug-view lib/discussion-state
         {:target (.getElementById js/document "debug")})


;; Find and bind arguments
(def arguments (.getElementsByClassName js/document "arguments"))

(defn register-arguments
  "Takes collection of all arguments in DOM and binds it to a view."
  [arguments]
  (loop [argument (first arguments)
         col      (rest arguments)]
    (when-not (nil? argument)
      (let [arg-text (.. argument -innerHTML)]
        (om/root views/argument-view {:text arg-text} {:target argument})
        (recur (first col) (rest col))))))

(register-arguments arguments)


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
