(ns discuss.core
  "Entrypoint to this application. Loads all requirements, and bootstraps the application."
  (:require [om.next :as om]
            [goog.dom :as gdom]
            [discuss.communication.lib :as comlib]
            [discuss.components.sidebar :as sidebar]
            [discuss.components.bubbles]
            [discuss.components.tooltip]
            [discuss.parser :as parser]
            [discuss.config :as config]
            [discuss.references.integration :as rint]
            [discuss.utils.common :as lib]
            [discuss.components.tooltip :as tooltip]
            [discuss.views :as views]))

(enable-console-print!)

;; Initialization
(defn ^:export main []
  (lib/log (str "Loaded " config/project " " config/version "-" config/build-commit))
  (om/add-root! parser/reconciler views/MainView (gdom/getElement (lib/prefix-name "main")))
  (rint/request-references)
  (comlib/init!))
(main)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
