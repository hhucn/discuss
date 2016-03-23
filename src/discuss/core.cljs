(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [discuss.communication :as com]
            [discuss.debug :as debug]
            [discuss.extensions]
            [discuss.integration]
            [discuss.lib :as lib]
            [discuss.views :as views]))

;; Initialization
;(com/init!)

;; Register
(om/root views/main-view lib/app-state
         {:target (.getElementById js/document "discuss-main")})

(om/root debug/debug-view lib/app-state
         {:target (.getElementById js/document "debug")})


;; Find and bind arguments
(def arguments (.getElementsByClassName js/document "arguments-toggle"))

(defn register-arguments
  "Takes collection of all arguments in DOM and binds it to a view."
  [arguments]
  (loop [argument (first arguments)
         col      (rest arguments)]
    (when-not (nil? argument)
        ;(.addEventListener argument "click" show-discuss-at-argument)
        (om/root views/argument-view lib/app-state {:target argument})
        (recur (first col) (rest col)))))

(register-arguments arguments)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
