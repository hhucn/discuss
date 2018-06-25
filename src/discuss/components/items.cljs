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

(defn- dispatch-click-fn
  "Dispatch which function should be applied if there is a click on an item."
  [url]
  (case url
    "login" (lib/change-view-next! :login)
    "back" (log/info "Not yet implemented")
    "add" (log/info "Not yet implemented")
    (comlib/ajax-get url (comlib/token-header) comlib/process-discussion-step)))

(s/fdef dispatch-click-fn
  :args (s/cat :url string?))

(defui Item
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
                             :onClick (partial dispatch-click-fn url)
                             :value url}]
                    " "
                    (vlib/safe-html (string/join (str " <i>" (t :common :and) "</i> ") htmls))]]))))
(def item (om/factory Item {:keyfn :url}))

(defui Items
  static om/IQuery
  (query [this]
         `[{:discussion/items ~(om/get-query Item)}])
  Object
  (render [this]
          (let [{:keys [discussion/items]} (om/props this)]
            (html [:div (map item items)]))))
(def items (om/factory Items))

