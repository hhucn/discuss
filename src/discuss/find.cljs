(ns discuss.find
  (:require [clojure.walk :refer [keywordize-keys]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.communication :as com]
            [discuss.lib :as lib]))

(def data (atom {}))

(defn statement-handler
  "Called when received a response in the search."
  [response]
  (let [res (lib/json->clj response)
        values (:values res)
        error (:error res)]
    (if (pos? (count error))
      (lib/error-msg! error)
      (do
        (lib/no-error!)
        (swap! data assoc :foo (keywordize-keys res))
        (println res)
        (println values)
        (println (get "values" res))
        ))))

(defn statement
  "Find related statements to given keywords."
  [keywords]
  (com/ajax-get "api/get/statements/1/3/a" {} statement-handler))

(defn item-view []
  (reify om/IRender
    (render [_]
      (dom/div nil "One element"))))

(defn view []
  (reify om/IRender
    (render [_]
      (dom/h4 nil "Hello World!"))))