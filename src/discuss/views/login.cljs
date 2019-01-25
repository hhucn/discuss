(ns discuss.views.login
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [discuss.communication.auth :as auth]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.views :as vlib]
            [discuss.views.alerts :as valerts]))

(defui LoginForm
  "Form with nickname and password input."
  static om/IQuery
  (query [this] [:layout/error])
  Object
  (render [this]
          (let [st (om/get-state this)
                nickname (or (:nickname st) "")
                password (or (:password st) "")]
            (html [:div
                   (vlib/view-header (t :common :login))
                   (valerts/error-alert (om/props this))
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
