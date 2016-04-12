(ns discuss.integration
  "Listen for mouse clicks, get references and highlight them in the article."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [put! chan <!]]
            [discuss.lib :as lib]
            [discuss.extensions]))

(defn get-selection
  "Return the stored selection of the user."
  []
  (get-in @lib/app-state [:user :selection]))

(defn has-selection? []
  (> (count (get-selection)) 0))

(defn save-selected-text
  "Get the users selection and save it."
  []
  (let [selection (str (.getSelection js/window))]
    (when (and (> (count selection) 0)
               (not= selection (get-selection)))
      (lib/update-state-item! :user :selection (fn [_] selection)))))

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

;;; Listener for mouse clicks
;; http://www.thesoftwaresimpleton.com/blog/2014/12/30/core-dot-async-dot-mouse-dot-down/
(let [clicks (listen (.getElementById js/document "discuss-text") "click")]
  (go (while true
        (<! clicks)
        (save-selected-text))))

;;; Integrate references and highlight them in the article
(defn process-references
  "Receives references through the API and prepares them for the next steps."
  [refs]
  (println "Received" (count refs) "references")
  (println refs))