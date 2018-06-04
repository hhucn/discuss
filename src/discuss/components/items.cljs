(ns discuss.components.items
  (:require [om.next :as om :refer-macros [defui]]
            [clojure.string :as string]
            [sablono.core :as html :refer-macros [html]]
            [discuss.communication.lib :as comlib]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defui ItemView
  static om/IQuery
  (query [this] [:htmls :url])
  Object
  (render [this]
          (let [{:keys [htmls url]} (om/props this)]
            (html [:div.radio
                   [:label
                    [:input {:type "radio"
                             :className (lib/prefix-name "dialog-items")
                             :name (lib/prefix-name "dialog-items-group")
                             :onClick #(comlib/ajax-get url nil comlib/process-discussion-step)
                             :value url}]
                    " "
                    (vlib/safe-html (string/join (str " <i>" (t :common :and) "</i> ") htmls))]]))))
(def item-view-next (om/factory ItemView {:keyfn :url}))

(defui ItemsView
  static om/IQuery
  (query [this]
         `[{:discussion/items ~(om/get-query ItemView)}])
  Object
  (render [this]
          (let [{:keys [discussion/items]} (om/props this)]
            (html [:div (map item-view-next items)]))))
(def items-view-next (om/factory ItemsView))

