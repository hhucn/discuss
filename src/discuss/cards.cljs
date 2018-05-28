(ns discuss.cards
  (:require [devcards.core :as dc :refer-macros [defcard dom-node]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.parser :as parser]
            #_[discuss.components.search.statements :as search :refer [SearchQuery Results]]
            [discuss.views :as views]
            [discuss.communication.auth :as auth]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.options :as options]))

(enable-console-print!)

(defcard shortcuts
  (html [:div.btn.btn-primary {:onClick #(auth/login "Christian" "iamgroot")} "Login"]))

#_(defcard-om discuss
  views/main-view
  lib/app-state)

#_(defonce test-data {:search/results [:foo :bar :baz]
                    :discussion/items [{:htmls ["the city should reduce the number of street festivals"], :texts ["the city should reduce the number of street festivals"], :url "town-has-to-cut-spending/attitude/36"} {:htmls ["we should shut down University Park"], :texts ["we should shut down University Park"], :url "town-has-to-cut-spending/attitude/37"} {:htmls ["we should close public swimming pools"], :texts ["we should close public swimming pools"], :url "town-has-to-cut-spending/attitude/38"}], :discussion/bubbles [{:type "system", :html "I want to talk about the position that.", :text "I want to talk about the position that.", :url nil}]})

#_(defcard discuss-next
  (dom-node
   (fn [_ node]
     (om/add-root! parser/reconciler views/main-view-next node))))

(defcard discussion-atoms
  "## Discussion Atoms")

(dc/defcard-om-next bubbles-view
  bubbles/BubblesView
  parser/reconciler)

(dc/defcard-om-next items-view
  views/ItemsView
  parser/reconciler)

(defcard login
  "## Login")

(dc/defcard-om-next login-form
  views/LoginForm
  parser/reconciler)

(defcard options
  "## Options")

(dc/defcard-om-next option-view
  options/Options
  parser/reconciler)

(defcard molecules
  "## View Molecules")

(dc/defcard-om-next discussion-elements
  views/DiscussionElements
  parser/reconciler)

(dc/defcard-om-next main-view
  views/MainView
  parser/reconciler)

(dc/defcard-om-next main-content-view
  views/MainContentView
  parser/reconciler)

(dc/defcard-om-next view-dispatcher
  views/ViewDispatcher
  parser/reconciler)

#_(dc/defcard-om-next footest
  views/items-view-next
  parser/reconciler)

#_(defcard-om-next footest-next
  views/items-view-next
  parser/reconciler)

#_(comlib/init!)

#_(defcard search-query
    (dom-node
     (fn [_ node]
       (om/add-root! parser/reconciler SearchQuery node)))
    {:inspect-data true})

#_(defcard-om search-query-now
  search/search-query-now
  lib/app-state)

#_(defcard search-results
  (dom-node
   (fn [_ node]
     (om/add-root! parser/reconciler Results node)))
  {:inspect-data true})

#_(defcard-om search-results-now
  search/results-now
  lib/app-state)

;; -----------------------------------------------------------------------------
;; Start devcards

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (when-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (html [:div "This is working"]) node)))
(main)
