(ns devcards.discuss.eden
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next om-next-root]]
            [discuss.eden.views :as eviews]))

(defcard add-new-eden-argument
  (eviews/eden-argument-form {:click-fn (fn [] (println "clicked"))}))
