(ns discuss.integration
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [put! chan <!]]
            [discuss.lib :as lib]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            ))

(defn selected-text []
  (let [selection (str (. js/window (getSelection)))]
    (.log js/console selection)))

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))


;(lib/log (.getElementsByTagName js/document "p"))
;(apply print (.getElementsByTagName js/document "p"))


(gobj/forEach (.getElementsByTagName js/document "p")
                     (fn [obj] (lib/log obj)))

(let [clicks (listen (.getElementById js/document "discuss-text") "click")
      col (.getElementsByTagName js/document "p")
      ;ps (gobj/forEach col #(listen % "click"))
      ;ps (map #(listen % "click") (js->clj (.getElementsByTagName js/document "p")))
      ]
  (go (while true
        (.log js/console (<! clicks)))))

;; http://www.thesoftwaresimpleton.com/blog/2014/12/30/core-dot-async-dot-mouse-dot-down/