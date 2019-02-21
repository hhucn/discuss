(ns discuss.parser
  (:require [om.next :as om]
            [cljs.spec.alpha :as s]
            [discuss.config :as config]))

(def init-data
  {:discussion/add-step :add/position
   :history/discussion-steps []
   :host/dbas config/remote-host
   :host/eden (or config/search-host nil)
   :issue/current-slug (:init config/api)
   :issue/info "Our town needs to cut spending. Please discuss ideas how this should be done."
   :issue/list [:list :of :issues]
   :issue/title "Town has to cut spending"
   :layout/add? false
   :layout/error nil
   :layout/lang :de
   :layout/loading? false
   :layout/sidebar? false
   :layout/title "discuss"
   :layout/view :create/argument
   :layout/view-next nil
   :search/results []
   :selection/current ""
   :user/avatar "img/profile.jpg"
   :user/logged-in? false
   :user/nickname "kangaroo"
   :user/token "razupaltuff"
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
   :eden/arguments []
   :clipboard/items []
   :references/all []
   :references/on-webpage []
   :references/usages []})

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

(s/def ::reconciler #(instance? om/Reconciler %))

(defonce reconciler
  (om/reconciler
   {:state  init-data
    :parser (om/parser {:read read :mutate mutate})}))
