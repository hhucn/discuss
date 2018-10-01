(ns discuss.components.avatar
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defui Avatar
  "Render the users' avatar."
  static om/IQuery
  (query [this]
         [:layout/lang :user/avatar :user/logged-in?])
  Object
  (render [this]
          (let [{:keys [user/avatar user/logged-in?]} (om/props this)]
            (html
             (when logged-in?
               [:div {:className (str (lib/prefix-name "avatar-main-wrapper") " text-center")}
                (if (string? avatar)
                  [:img {:className (str (lib/prefix-name "avatar-main") " img-responsive img-circle")
                         :src avatar}]
                  [:div {:className (lib/prefix-name "avatar-main")}
                   (vlib/fa-icon "fa-user-circle")])
                [:span.text-muted (str (t :common :hello) " " (lib/get-nickname) "!")]])))))
(def avatar (om/factory Avatar))
