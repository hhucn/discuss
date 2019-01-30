(ns discuss.utils.logging
  (:require [goog.log :as glog]
            [discuss.config :as config]
            [clojure.string :as string]
            [goog.string :refer [format]]
            [goog.string.format]))

(def logger (glog/getLogger config/project))

(def levels {:severe goog.debug.Logger.Level.SEVERE
             :warning goog.debug.Logger.Level.WARNING
             :info goog.debug.Logger.Level.INFO
             :config goog.debug.Logger.Level.CONFIG
             :fine goog.debug.Logger.Level.FINE
             :finer goog.debug.Logger.Level.FINER
             :finest goog.debug.Logger.Level.FINEST})

(defn- set-level! [level]
  (.setLevel logger (get levels level (:info levels))))

(defn- format-string
  "Takes a format string and sets all parameters if available. When only one
  parameter is provided, this value will be returned."
  ([fstring params]
   (prn params)
   (apply (partial format fstring) params))
  ([fstring] fstring))

(defn- make-logging-fn [f fstring params]
  (f logger (format-string fstring params)))

(defn info [fstring & params]
  (make-logging-fn glog/info fstring params))

(defn debug [fstring & params]
  (make-logging-fn glog/fine fstring params))

(defn error [fstring & params]
  (make-logging-fn glog/error fstring params))

(defn warning [fstring & params]
  (make-logging-fn glog/warning fstring params))

(defn fine [fstring & params]
  (make-logging-fn glog/fine fstring params))


;; -----------------------------------------------------------------------------
;; For Startup
(set-level! config/log-level)
