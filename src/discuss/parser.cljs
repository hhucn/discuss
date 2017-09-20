(ns discuss.parser
  (:require [om.next :as om]))

(def init-data {})

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


;; -----------------------------------------------------------------------------

(defonce reconciler
  (om/reconciler
   {:state  init-data
    :parser (om/parser {:read read :mutate mutate})}))

