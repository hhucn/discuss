(ns discuss.history
  (:require [discuss.lib :as lib]))

(def discussion-history (atom [@lib/app-state]))

(add-watch lib/app-state :history
           (fn [_ _ _ n]
             (when-not (= (last @discussion-history) n)
               (swap! discussion-history conj n))))

(defn back! []
  (when (> (count @discussion-history) 1)
    (dotimes [_ 5]                                          ; Workaround, because one action are currently x atom changes
      (swap! discussion-history pop))
    (reset! lib/app-state (last @discussion-history))))