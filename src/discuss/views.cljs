(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.lib :as lib]
            [discuss.communication :as com]
            [om-bootstrap.panel :as p]))


(defn clipboard-view []
  (reify om/IRender
    (render [_]
      (dom/div #js {:id "foo"}
               (dom/h5 nil "discuss")
               (dom/hr #js {:className "line-double"})
               (dom/div #js {:id (lib/prefix-name "clipboard-topic")})
               (dom/hr nil)
               (dom/div #js {:id (lib/prefix-name "clipboard-arguments")})))))

(defn item-view [item owner]
  (reify om/IRender
    (render [_]
      (dom/li #js {:className "pointer"
                   :onClick #(com/ajax-get (:url item))}
              (dom/input #js {:id        (:id item)
                              :type      "radio"
                              :className (lib/prefix-name "dialogue-items")
                              :name      (lib/prefix-name "dialogue-items-group")
                              :value     (:url item)})
              (:title item)))))

(defn main-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialogue-main")}
               (dom/h3 nil
                       (dom/i #js {:className "fa fa-comments"})
                       (str " " (:title data)))
               (dom/div #js {:className "text-center"}
                        (:intro data)
                        (dom/br nil)
                        (dom/strong nil (:info (:issues data))))
               (p/panel nil
                        (dom/h4 #js {:id (lib/prefix-name "dialogue-topic")
                                     :className "text-center"}
                                (:intro (:heading (:discussion data))))
                        (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                               (om/build-all item-view (:items data))))))))
