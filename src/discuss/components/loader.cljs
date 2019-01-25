(ns discuss.components.loader
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.views :as vlib]))

(defui Loader
  static om/IQuery
  (query [this] [:loader/text])
  Object
  (render
   [this]
   (let [{:keys [loader/text]} (om/props this)]
     (html [:div
            (vlib/fa-icon "fa-spinner fa-spin")
            " "
            (if (string? text)
              text
              "Loading...")]))))
(def loader (om/factory Loader))

(defui LoaderButton
  static om/IQuery
  (query [this] `[{:loader/text ~(om/get-query Loader)}])
  Object
  (render
   [this]
   (html [:div.btn.btn-default
          (loader (om/props this))])))
