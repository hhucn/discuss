(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [discuss.lib :as lib]
            [discuss.views :as views]))

(enable-console-print!)

;; Initialization
(lib/init!)

;; Register
(om/root views/main-view lib/discussion-state
         {:target (. js/document (getElementById "discuss-main"))})

;(om/root views/clipboard-view lib/app-state
;         {:target (. js/document (getElementById "discuss-clipboard"))})

(om/root views/debug-view lib/discussion-state
         {:target (. js/document (getElementById "debug"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
