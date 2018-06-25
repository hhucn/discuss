(ns discuss.utils.logging
  (:require [goog.log :as glog]
            [discuss.config :as config]))

(def logger (glog/getLogger config/project))

(def levels {:severe goog.debug.Logger.Level.SEVERE
             :warning goog.debug.Logger.Level.WARNING
             :info goog.debug.Logger.Level.INFO
             :config goog.debug.Logger.Level.CONFIG
             :fine goog.debug.Logger.Level.FINE
             :finer goog.debug.Logger.Level.FINER
             :finest goog.debug.Logger.Level.FINEST})

(defn set-level! [level]
  (.setLevel logger (get levels level (:info levels))))

(defn fmt [msgs]
  (apply str (interpose " " (map pr-str msgs))))

(defn info [& s]
  (let [msg (fmt s)]
    (glog/info logger msg)))

(defn debug [& s]
  (let [msg (fmt s)]
    (glog/fine logger msg)))

(defn error [& s]
  (let [msg (fmt s)]
    (glog/error logger msg)))


;; -----------------------------------------------------------------------------
;; For Startup
(set-level! config/log-level)
