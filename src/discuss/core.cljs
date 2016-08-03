(ns ^:figwheel-always discuss.core
  "Entrypoint to this application. Loads all requirements, and bootstraps the application."
  (:require [om.core :as om :include-macros true]
            [discuss.communication.main :as com]
            [discuss.components.sidebar :as sidebar]
            [discuss.components.bubbles]
            [discuss.debug :as debug]
            [discuss.references.integration]
            [discuss.utils.extensions]
            [discuss.utils.common :as lib]
            [discuss.components.tooltip :as tooltip]
            [discuss.views :as views]))

(enable-console-print!)

;; Initialization
(defn main []
  (com/init-with-references!))
;(main)

;; Register
(om/root views/main-view lib/app-state
         {:target (.getElementById js/document (lib/prefix-name "main"))})

(om/root sidebar/view lib/app-state
         {:target (.getElementById js/document (lib/prefix-name "sidebar"))})

(om/root tooltip/view {}
         {:target (.getElementById js/document (lib/prefix-name "tooltip"))})

(om/root debug/debug-view lib/app-state
         {:target (.getElementById js/document "debug")})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
