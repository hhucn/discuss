(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [discuss.views :as views]
            [discuss.lib :as lib]))

(enable-console-print!)

;; Register
(om/root views/main-view lib/app-state
         {:target (. js/document (getElementById "discuss-main"))})

(om/root views/clipboard-view lib/app-state
         {:target (. js/document (getElementById "discuss-clipboard"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
