(ns discuss.components.items
  (:require [om.next :as om :refer-macros [defui]]
            [clojure.string :as string]
            [sablono.core :as html :refer-macros [html]]
            [discuss.communication.lib :as comlib]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.utils.logging :as log]
            [cljs.spec.alpha :as s]))

(defn- item-click
  "Store the currently clicked url in the app-state."
  [url]
  (lib/save-clicked-item! url)
  (comlib/ajax-get url (comlib/token-header) comlib/process-and-set-items-and-bubbles))

(defn- dispatch-click-fn
  "Dispatch which function should be applied if there is a click on an item."
  [url]
  (case url
    "login" (lib/change-view! :login)
    "back" (log/info "Not yet implemented")
    "add" (lib/show-add-form!)
    (item-click url)))

(s/fdef dispatch-click-fn
  :args (s/cat :url string?))

(defui Item
  static om/IQuery
  (query [this] [:htmls :texts :url])
  Object
  (render [this]
          (let [{:keys [htmls url]} (om/props this)]
            (html [:div.radio
                   [:label
                    [:input {:type "radio"
                             :className (lib/prefix-name "dialog-items")
                             :name (lib/prefix-name "dialog-items-group")
                             :onClick (partial dispatch-click-fn url)
                             :value url}]
                    " "
                    (vlib/safe-html (string/join (str " <i>" (t :common :and) "</i> ") htmls))]]))))
(def item (om/factory Item {:keyfn :url}))

(defui Items
  static om/IQuery
  (query [this]
         `[:layout/lang
           {:discussion/items ~(om/get-query Item)}])
  Object
  (render [this]
          (let [{:keys [discussion/items]} (om/props this)]
            (html [:div (map item items)]))))
(def items (om/factory Items))
(om/get-query Items)
