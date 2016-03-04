(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-bootstrap.panel :as p]
            [cljs.pprint :refer [pprint]]
            [goog.string :as gstring]
            [discuss.communication :as com]
            [discuss.lib :as lib]))


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
                       (str " " (get-in data [:layout :title])))
               (dom/div #js {:className "text-center"}
                        (:intro (:layout data))
                        (dom/br nil)
                        (dom/strong nil (:info (:issues data))))
               (p/panel nil
                        (dom/h4 #js {:id (lib/prefix-name "dialogue-topic")
                                     :className "text-center"}
                                (get-in data [:discussion :heading :intro])
                                (get-in data [:discussion :heading :bridge])
                                (get-in data [:discussion :heading :outro])
                                )
                        (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                               (om/build-all item-view (:items data))))))))


(defn debug-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h4 nil "Control")
               (dom/button #js {:onClick (fn [_e]
                                           (when (> (count @lib/app-history) 1)
                                             (swap! lib/app-history pop)
                                             (reset! lib/app-state (last @lib/app-history))))}
                           "Hi")

               (dom/h4 nil "Last API call")
               (dom/pre nil (get-in data [:debug :last-api]))

               (dom/h4 nil "Last response")
               ;(pprint data)
               (dom/pre nil
                        (apply dom/ul nil
                               (map (fn [[k v]] (dom/li nil (str k ": " v))) (get-in data [:debug :response]))))
               ))))