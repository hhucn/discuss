(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [discuss.communication :as com]
            [discuss.debug :as debug]
            [discuss.extensions]
            [discuss.integration]
            [discuss.lib :as lib]
            [discuss.test :as test]
            [discuss.views :as views]))

;; Initialization
;(com/init!)

;; Register
(om/root views/main-view lib/app-state
         {:target (.getElementById js/document "discuss-main")})

(om/root debug/debug-view lib/app-state
         {:target (.getElementById js/document "debug")})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (test/run)
  )
