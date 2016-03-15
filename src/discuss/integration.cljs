(ns discuss.integration
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [put! chan <!]]
            [discuss.lib :as lib]
            [discuss.extensions]
            [goog.dom :as gdom]
            [goog.object :as gobj]))

(defn selected-text []
  (let [selection (str (. js/window (getSelection)))]
    (lib/log selection)))

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

(def args (.getElementsByClassName js/document "arguments"))

(gobj/forEach args #(lib/log (type %)))

(let [clicks (listen (.getElementById js/document "discuss-text") "click")
      col (.getElementsByTagName js/document "p")
      ;ps (gobj/forEach col #(listen % "click"))
      ;ps (map #(listen % "click") (js->clj (.getElementsByTagName js/document "p")))
      ]
  (go (while true
        (<! clicks)
        (.log js/console (selected-text)))))

;; http://www.thesoftwaresimpleton.com/blog/2014/12/30/core-dot-async-dot-mouse-dot-down/