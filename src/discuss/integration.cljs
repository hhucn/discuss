(ns discuss.integration
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [put! chan <!]]
            [discuss.lib :as lib]
            [discuss.extensions]
            [goog.dom :as gdom]
            [goog.object :as gobj]))

(defn selected-text []
  (let [selection (str (.getSelection js/window))]
    (when (> (count selection) 0)
      (lib/log selection))))

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

(let [clicks (listen (.getElementById js/document "discuss-text") "click")]
  (go (while true
        (<! clicks)
        (selected-text))))

;; http://www.thesoftwaresimpleton.com/blog/2014/12/30/core-dot-async-dot-mouse-dot-down/