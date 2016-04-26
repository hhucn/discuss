(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [discuss.communication :as com]
            [discuss.debug :as debug]
            [discuss.extensions]
            [discuss.lib :as lib]
            [discuss.views :as views]))

;; Initialization
(com/init!)
(discuss.auth/login "christian" "christian")
(com/post-origin-get-references)

;; Register
(om/root views/main-view lib/app-state
         {:target (.getElementById js/document (lib/prefix-name "main"))})

(om/root views/sidebar-view lib/app-state
         {:target (.getElementById js/document (lib/prefix-name "sidebar"))})

(om/root views/tooltip-view {}
         {:target (.getElementById js/document (lib/prefix-name "tooltip"))})

(om/root debug/debug-view lib/app-state
         {:target (.getElementById js/document "debug")})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  ; (test/run)
  )
