(ns discuss.parser
  (:require [om.next :as om]))

(def init-data {:search/results []
                :layout/error nil
                :layout/title "discuss"
                :layout/view :default
                :layout/lang :en
                :issue/title "Town has to cut spending"
                :issue/info "Our town needs to cut spending. Please discuss ideas how this should be done."
                :selection/current "This is a selected text passage"
                :site/origin {}
                :discussion/items [{:htmls ["the city should reduce the number of street festivals"],
                                    :texts ["the city should reduce the number of street festivals"],
                                    :url "town-has-to-cut-spending/attitude/36"}
                                   {:htmls ["we should shut down University Park"],
                                    :texts ["we should shut down University Park"],
                                    :url "town-has-to-cut-spending/attitude/37"}
                                   {:htmls ["we should close public swimming pools"],
                                    :texts ["we should close public swimming pools"],
                                    :url "town-has-to-cut-spending/attitude/38"}]
                :discussion/bubbles [{:type :user
                                      :html "We should shut down University Park."
                                      :text "We should shut down University Park."
                                      :url "/town-has-to-cut-spending/attitude/12"}
                                     {:type :status
                                      :html "Now"
                                      :text "Now"
                                      :url nil}
                                     {:type :system
                                      :html "What is your most important reason why <span data-argumentation-type=\"position\">we should shut down University Park</span> <span class='text-success'>holds</span>? <br>Because..."
                                      :text "What is your most important reason why we should shut down University Park holds? Because..."
                                      :url nil}]
                :clipboard/items [{:title "This is an item from the clipboard"}
                                  {:title "And yet another item"}]})

;; -----------------------------------------------------------------------------
;; Parsing

(defn get-data [state key]
  (let [st @state]
    (into [] (map #(get-in st %)) (get st key))))

(defmulti read om/dispatch)
(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

#_(defmethod read :item/by-url
  [{:keys [state]} _ {:keys [url]}]
  (let [st @state
        item (for [item (get st :discussion/items) :when (= url (:url item))] item)]
    item))
;; (read {:state (om/app-state scarf.core/reconciler)} :color/by-id {:id 1})


(defmulti mutate om/dispatch)
(defmethod mutate 'search/results [{:keys [state]} _ {:keys [results]}]
  {:action (fn [] (swap! state assoc :search/results results))})

(defmethod mutate 'discussion/items [{:keys [state]} _ {:keys [items]}]
  {:action (fn [] (swap! state assoc :discussion/items items))})

(defmethod mutate 'discussion/bubbles [{:keys [state]} _ {:keys [bubbles]}]
  {:action (fn [] (swap! state assoc :discussion/bubbles bubbles))})

(defmethod mutate 'layout/view [{:keys [state]} _ {:keys [view]}]
  {:action (fn [] (swap! state assoc :layout/view view))})
(defmethod mutate 'layout/add? [{:keys [state]} _ {:keys [add?]}]
  {:action (fn [] (swap! state assoc :layout/add? add?))})
(defmethod mutate 'layout/lang [{:keys [state]} _ {:keys [lang]}]
  {:action (fn [] (swap! state assoc :layout/lang lang))})

;; -----------------------------------------------------------------------------

(defonce reconciler
  (om/reconciler
   {:state  init-data
    :parser (om/parser {:read read :mutate mutate})}))
