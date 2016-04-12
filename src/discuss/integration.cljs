(ns discuss.integration
  "Listen for mouse clicks, get references and highlight them in the article."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [goog.dom :as gdom]
    [goog.events :as events]
    [cljs.core.async :refer [put! chan <!]]
    [clojure.string :as string]
    [discuss.lib :as lib]
    [discuss.extensions])
  (:import [goog.dom]))

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
;; So I have to iterate through all DOM elements...?
;; http://stackoverflow.com/questions/4256339/javascript-how-to-loop-through-all-dom-elements-on-a-page
(defn convert-reference [ref]
  (let [body js/document.body.innerHTML
        ;tspan (str "<span class='arguments'>" ref "</span>")
        span (gdom/createElement "span")
        tspan "foo"
        regex-ref (re-pattern ref)
        nbody (string/replace body regex-ref (.-innerHTML span))]
    ;(gdom/setTextContent span ref)
    (println "Convert...")
    ;(set! (.. js/document -body -innerHTML) nbody)

    ))

(defn process-references
  "Receives references through the API and prepares them for the next steps."
  [refs]
  (println "Received" (count refs) "references")
  (println refs)
  (convert-reference "Debug"))