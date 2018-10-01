(ns discuss.views
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.clipboard :as clipboard]
            [discuss.components.items :as items]
            [discuss.components.navigation :as nav]
            [discuss.components.options :as options]
            [discuss.components.search.statements :as search]
            [discuss.components.avatar :as avatar]
            [discuss.communication.auth :as auth]
            [discuss.communication.lib :as comlib]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.views.add :as vadd]
            [discuss.parser :as parser]
            [discuss.views.alerts :as valerts]))

(defui LoginForm
  "Form with nickname and password input."
  Object
  (render [this]
          (let [st (om/get-state this)
                nickname (or (:nickname st) "")
                password (or (:password st) "")]
            (html [:div (vlib/view-header (t :common :login))
                   (valerts/error-alert st)
                   [:p.text-center (t :login :hhu-ldap)]
                   [:div.input-group
                    [:span.input-group-addon (vlib/fa-icon "fa-user fa-fw")]
                    [:input.form-control {:onChange #(om/update-state! this assoc :nickname (.. % -target -value))
                                          :value nickname
                                          :placeholder (t :login :nickname)}]]
                   [:div.input-group
                    [:span.input-group-addon (vlib/fa-icon "fa-key fa-fw")]
                    [:input.form-control {:onChange #(om/update-state! this assoc :password (.. % -target -value))
                                          :value password
                                          :type :password
                                          :placeholder (t :login :password)}]]
                   [:button.btn.btn-default {:onClick #(auth/login nickname password)
                                             :disabled (or (empty? nickname)
                                                           (empty? password))}
                    (t :common :login)]]))))
(def login-form (om/factory LoginForm))

;; ----------

(defn- close-button-next
  "Close current panel and switch view."
  []
  (html [:div [:br]
         [:div.text-center
          (bs/button-default-sm #(lib/change-view-next! :default) (vlib/fa-icon "fa-times") (t :common :close :space))]]))

(defn- control-elements-next
  "Back and restart button."
  []
  (html [:div [:hr]
         [:div.row
          [:div {:className "col-md-offset-4 col-sm-offset-4 col-xs-offset-4 col-md-4 col-sm-4 col-xs-4 text-center"}
           [:button.btn.btn-default.btn-sm {:onClick parser/back!
                                            :disabled  (neg? (count (parser/mutation-history parser/reconciler)))}
            (vlib/fa-icon "fa-step-backward")
            (t :common :back :space)]]
          [:div.col-md-4.col-sm-4.col-xs-4.text-right
           (bs/button-default-sm comlib/init! (vlib/fa-icon "fa-refresh") (t :discussion :restart :space))]]]))

(defui DiscussionElements
  static om/IQuery
  (query [this]
         `[{:discussion/items ~(om/get-query items/Items)}
           {:discussion/bubbles ~(om/get-query bubbles/BubblesView)}])
  Object
  (render [this]
          (html [:div
                 (bubbles/bubbles-view-next (om/props this))
                 (items/items (om/props this))])))
(def discussion-elements-next (om/factory DiscussionElements))

(defui ViewDispatcher
  static om/IQuery
  (query [this]
         `[:layout/view :layout/lang
           {:discussion/items ~(om/get-query DiscussionElements)}
           {:discussion/bubbles ~(om/get-query DiscussionElements)}])
  Object
  (render [this]
          (let [{:keys [layout/view]} (om/props this)]
            (html [:div.panel.panel-default
                   [:div.panel-body
                    (case view
                      :login (login-form)
                      :options (options/options (om/props this))
                      ;; TODO: :reference-usages (om/build ref/usages-view data)
                      ;; TODO: :reference-create-with-ref (om/build ref/create-with-reference-view data)
                      ;; TODO: :find (om/build find/view data)
                      (discussion-elements-next (om/props this)))
                    (if (some #{view} [:login :options :find :reference-usages])
                      (close-button-next)
                      (control-elements-next))]]))))
(def view-dispatcher-next (om/factory ViewDispatcher))

(defui MainView
  static om/IQuery
  (query [this]
         `[:issue/info :layout/add? :discussion/add-step :layout/view :layout/title :layout/lang
           :user/avatar :user/nickname :user/logged-in?
           {:discussion/items ~(om/get-query ViewDispatcher)}
           {:discussion/bubbles ~(om/get-query ViewDispatcher)}
           {:discussion/bubbles ~(om/get-query vadd/StatementForm)}
           {:discussion/bubbles ~(om/get-query vadd/PositionForm)}
           {:clipboard/items ~(om/get-query clipboard/Clipboard)}])
  Object
  (render [this]
          (let [{:keys [issue/info layout/add? layout/title discussion/add-step]} (om/props this)]
            (html
             [:div#discuss-dialog-main
              (avatar/avatar (om/props this))
              [:h4 (vlib/logo)
               " "
               [:span.pointer {:data-toggle   "collapse"
                               :data-target   (str "#" (lib/prefix-name "dialog-collapse"))
                               :aria-expanded "true"
                               :aria-controls (lib/prefix-name "dialog-collapse")}
                title]]
              [:div.text-center
               (t :discussion :current)
               [:br]
               [:strong info]]
              (view-dispatcher-next (om/props this))
              (when add?
                [:div
                 (if (= :add/position add-step)
                   (vadd/position-form (om/props this))
                   (vadd/statement-form (om/props this)))
                 (search/results (om/props this))])
              (nav/nav)
              [:br]
              (clipboard/clipboard (om/props this))]))))
(def main-view-next (om/factory MainView))
