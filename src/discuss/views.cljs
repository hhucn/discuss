(ns discuss.views
  (:require  [om.core :as om :include-macros true]
             [om.dom :as dom :include-macros true]
             [discuss.lib :as lib]
             [om-bootstrap.button :as bs3]))


(defn clipboard-view []
  (reify om/IRender
    (render [_]
      (dom/div #js {:id "foo"}
               (dom/h5 nil "discuss")
               (dom/hr #js {:addClass "line-double"})
               (dom/div #js {:id (lib/prefix-name "clipboard-topic")})
               (dom/hr nil)
               (dom/div #js {:id (lib/prefix-name "clipboard-arguments")})))))

(defn item-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/span nil (:uid data)))))

(defn items-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "items-main")}
               (om/build-all item-view (:items data))))))

(defn main-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialogue-main")}
               (dom/h3 nil
                       (:title data))
               (dom/div {:id (lib/prefix-name "dialogue-topic")}
                        (:topic data))
               (dom/div #js {:id (lib/prefix-name "items-main")}
                        (om/build-all item-view (:items data)))))))






;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last ", " first (middle-name contact)))

(defn student-view [student owner]
  (reify om/IRender
    (render [_]
      (dom/li nil (display-name student)))))

(defn professor-view [professor owner]
  (reify om/IRender
    (render [_]
      (dom/li nil
              (dom/div nil (display-name professor))
              (dom/label nil "Classes")
              (apply dom/ul nil
                     (map #(dom/li nil (om/value %)) (:classes professor)))))))


(defmulti entry-view (fn [person _] (:type person)))
(defmethod entry-view :student
  [person owner] (student-view person owner))
(defmethod entry-view :professor
  [person owner] (professor-view person owner))