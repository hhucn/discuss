(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.pprint :refer [pprint]]
            [discuss.communication :as com]
            [discuss.history :as history]
            [discuss.lib :as lib]))

;; Elements
(defn control-buttons []
  (dom/div #js {:className "text-center"}
           (dom/h3 nil
                   (dom/i #js {:className "fa fa-angle-double-left pointer"
                               :onClick lib/init!})
                   " "
                   (dom/i #js {:className "fa fa-angle-left pointer"
                               :onClick history/back!})
                   " "
                   (dom/i #js {:className "fa fa-angle-right pointer"}))))

(defn login-view-buttons [data _owner]
  (dom/div #js {:className "text-muted"}
             (dom/div #js {:className "row"}
                       (if (get-in data [:extras :logged_in])
                         (do
                           (dom/div #js {:className "col-md-6"}
                                     (str "Logged in as " (get-in data [:extras :users_name])))
                           (dom/div #js {:className "col-md-6 text-right"}
                                     "Logout"))
                         (dom/div #js {:className "col-md-offset 6 text-right"}
                                   "Login")))))

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

(defn item-view [item _owner]
  (reify om/IRender
    (render [_]
      (dom/li #js {:className "pointer"
                   :onClick #(com/ajax-get (:url item))}
              (dom/input #js {:id        (:id item)
                              :type      "radio"
                              :className (lib/prefix-name "dialogue-items")
                              :name      (lib/prefix-name "dialogue-items-group")
                              :value     (:url item)})
              " "
              (:title item)))))

(defn main-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialogue-main")
                    :className "container"}
               (dom/h3 nil
                       (dom/i #js {:className "fa fa-comments"})
                       (str " " (get-in data [:layout :title])))
               (dom/div #js {:className "text-center"}
                        (:intro (:layout data))
                        (dom/br nil)
                        (dom/strong nil (:info (:issues data))))
               (dom/div #js {:className "panel panel-default"}
                        (dom/div #js {:className "panel-body"})
                            (dom/h4 #js {:id (lib/prefix-name "dialogue-topic")
                                         :className "text-center"}
                                    (get-in data [:discussion :heading :intro])
                                    (get-in data [:discussion :heading :bridge])
                                    (get-in data [:discussion :heading :outro])
                                    )
                            (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                                   (om/build-all item-view (:items data)))
                            (control-buttons)
                            (login-view-buttons data owner))))))

(defn debug-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h4 nil "Last API call")
               (dom/pre nil (get-in data [:debug :last-api]))

               (dom/h4 nil "Last response")
               ;(pprint data)
               (dom/pre nil
                        (apply dom/ul nil
                               (map (fn [[k v]] (dom/li nil (str k "\t\t" v))) (get-in data [:debug :response]))))))))