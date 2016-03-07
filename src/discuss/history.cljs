(ns discuss.history
  (:require [discuss.lib :as lib]))

(def app-history (atom [@lib/app-state]))

(add-watch lib/app-state :history
           (fn [_ _ _ n]
             (when-not (= (last @app-history) n)
               (swap! app-history conj n))))

(defn back! []
  (when (> (count @app-history) 1)
    (dotimes [_ 3]                                          ; Workaround, because one action are currently 3 atom changes
      (swap! app-history pop))
    (reset! lib/app-state (last @app-history))))