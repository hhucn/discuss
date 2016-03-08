(ns discuss.debug
  "Show information for debugging."
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn debug-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h4 nil "Last API call")
               (dom/pre nil (get-in data [:debug :last-api]))

               (dom/h4 nil "Last response")
               ;(pprint data)
               (dom/pre nil
                        (apply dom/ul nil
                               (map (fn [[k v]] (dom/li nil (str k "\t\t" v))) (get-in data [:debug :response]))))))))