(ns discuss.core
  "Entrypoint to this application. Loads all requirements, and bootstraps the application."
  (:require [goog.dom :as gdom]
            [discuss.communication.main :as com]
            [discuss.components.sidebar :as sidebar]
            [discuss.components.bubbles]
            [discuss.config :as config]
            [discuss.references.integration]
            [discuss.utils.common :as lib]
            [discuss.components.tooltip :as tooltip]
            [discuss.views :as views]
            [om.core :as om]))

(enable-console-print!)

;; Initialization
(defn ^:export main []
  (lib/log (str "Loaded " config/project " " config/version))
  (com/init-with-references!))
(main)

;; Register
(defn register-view
  "Register view in the current DOM."
  [[div-name view]]
  (when-let [div (gdom/getElement (lib/prefix-name div-name))]
    (om/root view lib/app-state {:target div})
    #_(nom/add-root! parser/reconciler view div)))

(doall (map register-view [["main" views/main-view]
                           ["sidebar" sidebar/view]
                           ["tooltip" tooltip/view]]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
