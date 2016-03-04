(ns discuss.history
  (:require [discuss.lib :as lib]))

(defn back! [_e]
  (when (> (count @lib/app-history) 1)
    (swap! lib/app-history pop)
    (reset! lib/app-state (last @lib/app-history))))