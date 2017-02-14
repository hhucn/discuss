(ns discuss.core
  "Entrypoint to this application. Loads all requirements, and bootstraps the application."
  (:require [om.core :as om :include-macros true]
            [goog.dom :as gdom]
            [discuss.communication.main :as com]
            [discuss.components.sidebar :as sidebar]
            [discuss.components.bubbles]
            [discuss.config :as config]
            [discuss.debug :as debug]
            [discuss.references.integration]
            [discuss.utils.common :as lib]
            [discuss.components.contribute :as contribute]
            [discuss.components.tooltip :as tooltip]
            [discuss.views :as views]))

(enable-console-print!)

;; Initialization
(defn ^:export main []
  (lib/log (str "Loaded " config/project " " config/version))
  (com/init-with-references!))
(main)

;; Register
(defn register-view
  "Register view in the current DOM."
  [[div-name view state]]
  (when-let [div (gdom/getElement (lib/prefix-name div-name))]
    (om/root view state {:target div})))

(doall (map register-view [["main" views/main-view lib/app-state]
                           ["sidebar" sidebar/view lib/app-state]
                           ["tooltip" tooltip/view lib/app-state]
                           ["contribute" contribute/view {}]
                           ["debug" debug/debug-view lib/app-state]]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
