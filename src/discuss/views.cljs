(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.pprint :as pprint]
            [discuss.communication :as com]
            [discuss.extensions]
            [discuss.history :as history]
            [discuss.lib :as lib]
            [discuss.auth :as auth]))

;; Auxiliary functions
(defn safe-html
  "Creates DOM element with interpreted HTML."
  [string]
  (dom/span #js {:dangerouslySetInnerHTML #js {:__html string}}))

(defn get-bubble-class [bubble]
  "Check bubble type and return a class-string to match the CSS styles."
  (cond
    (:is_user bubble)   "bubble-user"
    (:is_system bubble) "bubble-system"
    (:is_status bubble) "bubble-status text-center"))

(defn display
  "Toggle display view."
  [show]
  (if show
    #js {}
    #js {:display "none"}))


;; Elements
(defn loading-element []
  (when (lib/loading?)
    (dom/div #js {:className "loader"}
             (dom/svg #js {:className "circular" :viewBox "25 25 50 50"}
                      (dom/circle #js {:className "path" :cx "50" :cy "50" :r "20" :fill "none" :strokeWidth "5" :strokeMiterlimit "10"})))))

(defn control-elements []
  (dom/div #js {:className "text-center"}
           (dom/h4 nil
                   (dom/i #js {:className "fa fa-angle-double-left pointer"
                               :onClick com/init!})
                   " "
                   (dom/i #js {:className "fa fa-angle-left pointer"
                               :onClick history/back!})
                   " "
                   (dom/i #js {:className "fa fa-angle-right pointer"}))))

(defn login-view-buttons [data]
  (dom/div #js {:className "text-muted"}
           (dom/div #js {:className "row"}
                    (if (lib/logged-in?)
                      (dom/div #js {:className "col-md-5"}
                               (str "Logged in as " (get-in data [:user :nickname])))
                      (dom/div #js {:className "col-md-5"}))
                    (dom/div #js {:className "col-md-2 text-center"}
                             (when (lib/loading?)
                               (loading-element)))

                    (if (lib/logged-in?)
                      (dom/div #js {:className "col-md-5 text-right pointer"
                                    :onClick auth/logout}
                               "Logout")
                      (dom/div #js {:className "col-md-5 text-right pointer"
                                    :onClick #(lib/change-view! :login)}
                               "Login")))))

(defn login-form []
  (dom/div nil
           (dom/div #js {:className "form-group"}
                    (dom/label #js {:htmlFor (lib/prefix-name "login-nickname")} "Nickname")
                    (dom/input #js {:id (lib/prefix-name "login-nickname")
                                    :className "form-control"
                                    :placeholder "nickname"}))
           (dom/div #js {:className "form-group"}
                    (dom/label #js {:htmlFor (lib/prefix-name "login-password")} "Password")
                    (dom/input #js {:id (lib/prefix-name "login-password")
                                    :className "form-control"
                                    :type "password"
                                    :placeholder "password"}))
           (dom/button #js {:className "btn btn-default"
                            :onClick   #(auth/login (lib/get-value-by-id "login-nickname") (lib/get-value-by-id "login-password"))}
                       "Login")
           (dom/div #js {:className "text-center text-muted pointer"
                         :onClick #(lib/change-view! :discussion)}
                    "Back")))


;; Views
(defn clipboard-view []
  (reify om/IRender
    (render [_]
      (dom/div #js {:id "foo"}
               (dom/h5 nil "discuss")
               (dom/hr #js {:className "line-double"})
               (dom/div #js {:id (lib/prefix-name "clipboard-topic")})
               (dom/hr nil)
               (dom/div #js {:id (lib/prefix-name "clipboard-arguments")})))))

(defn bubble-view [bubble]
  (reify om/IRender
    (render [_]
      (let [bubble-class (get-bubble-class bubble)]
        (dom/li #js {:className bubble-class}
                (dom/div #js {:className "avatar"})
                (dom/p #js {:className "messages"}
                       (safe-html (:message bubble))))))))

(defn bubbles-view []
  (apply dom/ol #js {:className "bubbles"}
         (om/build-all bubble-view (lib/get-bubbles))))

(defn item-view [item _owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "radio"}
               (dom/label #js {}
                          (dom/input #js {:id        (:id item)
                                          :type      "radio"
                                          :className (lib/prefix-name "dialogue-items")
                                          :name      (lib/prefix-name "dialogue-items-group")
                                          :onClick   #(com/item-click (:id item) (:url item))
                                          :value     (:url item)})
                          " "
                          (safe-html (:title item)))))))

(defn items-view [data]
  (dom/div nil
           (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                  (om/build-all item-view (:items data)))))

(defn discussion-elements [data]
  (dom/div nil
           (bubbles-view)
           (items-view data)
           (control-elements)
           (login-view-buttons data)))

(defn add-element
  "Show form to add a new statement."
  [data]
  (dom/div #js {:className "panel panel-default"}
           (dom/div #js {:className "panel-body"}
                    (dom/h4 #js {:className "text-center"} (get-in data [:layout :add-text]))
                    (dom/h5 #js {:className "text-center"} (safe-html (get-in data [:discussion :add_premise_text])))
                    (dom/div #js {:className "form-group"}
                             (dom/label #js {:htmlFor (lib/prefix-name "add-element")} (get-in data [:discussion :heading :outro]))
                             (dom/input #js {:id (lib/prefix-name "add-element")
                                             :className "form-control"}))
                    (dom/button #js {:className "btn btn-default"
                                     :onClick   #(com/add-start-statement (lib/get-value-by-id "add-element"))}
                                "Submit"))))

(defn main-content-view
  [data]
   (dom/div nil
            (dom/div #js {:className "text-center"}
                     (get-in data [:layout :intro])
                     (dom/br nil)
                     (dom/strong nil (get-in data [:issues :info])))
            (dom/div #js {:className "panel panel-default"}
                     (dom/div #js {:className "panel-body"}
                              (let [view (get-in data [:layout :template])]
                                (cond
                                  (= view :login) (login-form)
                                  :else (discussion-elements data)))))
            (when (get-in data [:layout :add?])
              (add-element data))))

(defn main-view [data]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialogue-main")}
               (dom/h4 nil
                       (dom/i #js {:className "fa fa-comments"})
                       " "
                       (get-in data [:layout :title]))
               (main-content-view data)))))

(defn toggle-show [show] (if show false true))

(defn argument-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:show false
       :text ""})
    om/IRenderState
    (render-state [_ {:keys [show text]}]
      (when (= text "")                                     ; Set initial text to be displayed
        (om/set-state! owner :text (:text data)))
      (dom/span nil
                " "
                (dom/i #js {:className "pointer fa fa-comments"
                            :onClick   #(om/set-state! owner :show (toggle-show show))})
                (when show
                  (main-content-view data))))))