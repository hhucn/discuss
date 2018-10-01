(ns discuss.parser
  (:require [om.next :as om]
            [cljs.spec.alpha :as s]))

(def init-data {:api/last-call ""
                :search/results []
                :layout/add? false
                :layout/error nil
                :layout/error? false  ;; for legacy support
                :layout/loading? false
                :layout/sidebar? false
                :layout/title "discuss"
                :layout/view :default
                :layout/view-next nil
                :layout/lang :en
                :issue/title "Town has to cut spending"
                :issue/info "Our town needs to cut spending. Please discuss ideas how this should be done."
                :issue/list [:list :of :issues]
                :user/nickname "kangaroo"
                :user/token "razupaltuff"
                :user/logged-in? false
                :user/avatar "img/profile.jpg"
                :selection/current "This is a selected text passage"
                :discussion/add-step :add/position
                :discussion/items [{:htmls ["the city should reduce the number of street festivals"],
                                    :texts ["the city should reduce the number of street festivals"],
                                    :url "/town-has-to-cut-spending/attitude/36"}
                                   {:htmls ["we should shut down University Park"],
                                    :texts ["we should shut down University Park"],
                                    :url "/town-has-to-cut-spending/attitude/37"}
                                   {:htmls ["we should close public swimming pools"],
                                    :texts ["we should close public swimming pools"],
                                    :url "/town-has-to-cut-spending/attitude/38"}]
                :discussion/bubbles [{:type "user"
                                      :html "We should shut down University Park."
                                      :text "We should shut down University Park."
                                      :url "/town-has-to-cut-spending/attitude/12"}
                                     {:type "status"
                                      :html "Now"
                                      :text "Now"
                                      :url nil}
                                     {:type "system"
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

(defmulti mutate om/dispatch)
(defmethod mutate :default
  [{:keys [state] :as env} field {:keys [value]}]
  {:action (fn [] (swap! state assoc (keyword field) value))})

;; -----------------------------------------------------------------------------

(def reconciler-history (atom []))

(defn mutation-history
  "Return uuid-list of the reconciler's history."
  [reconciler]
  (-> reconciler :config :history .-arr js->clj))

(s/def ::reconciler #(instance? om/Reconciler %))
(s/fdef mutation-history
  :args (s/cat :reconciler ::reconciler)
  :ret (s/coll-of uuid?))

(defonce reconciler
  (om/reconciler
   {:state  init-data
    :parser (om/parser {:read read :mutate mutate})
    :tx-listen (fn [tx-data _]
                 (reset! reconciler-history (mutation-history (:reconciler tx-data))))}))
(defn back!
  "Travel one unit back in time!"
  []
  (when-let [uuid-from-history (last @reconciler-history)]
    (let [state-from-history (om/from-history reconciler uuid-from-history)]
      (swap! reconciler-history pop)
      (reset! (om/app-state reconciler) state-from-history))))
