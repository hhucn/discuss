(ns discuss.parser
  (:require [om.next :as om]))

(def init-data {:search/results []})

;; -----------------------------------------------------------------------------
;; Parsing

(defmulti read (fn [env key params] key))
(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

(defmulti mutate om/dispatch)
(defmethod mutate 'search/results [{:keys [state]} _ {:keys [results]}]
  {:action (fn [] (swap! state assoc :search/results results))})


;; -----------------------------------------------------------------------------

(defonce reconciler
  (om/reconciler
   {:state  init-data
    :parser (om/parser {:read read :mutate mutate})}))

