(ns discuss.history
  (:require [discuss.lib :as lib]))

(def discussion-history (atom [@lib/discussion-state]))

(add-watch lib/discussion-state :history
           (fn [_ _ _ n]
             (when-not (= (last @discussion-history) n)
               (swap! discussion-history conj n))))

(defn back! []
  (when (> (count @discussion-history) 1)
    (dotimes [_ 3]                                          ; Workaround, because one action are currently 3 atom changes
      (swap! discussion-history pop))
    (reset! lib/discussion-state (last @discussion-history))))