(ns discuss.components.bubbles
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.common :as lib]
            [discuss.communication.lib :as comlib]
            [discuss.utils.views :as vlib]
            [cljs.spec.alpha :as s]
            [discuss.parser :as parser]))

(defn- get-bubble-class-next [bubble-type]
  "Check bubble type and return a class-string to match the CSS styles."
  (case bubble-type
    "user" "bubble-user"
    "system" "bubble-system"
    "status" "bubble-status text-center"
    "info" "bubble-info text-center"
    ""))

(s/fdef get-bubble-class-next
        :args (s/cat :bubble-type keyword?)
        :ret string?)

(defn dispatch-link-destination-next
  "Look into url field and return its corresponding function to be called onClick
  on the element."
  [url]
  (case url
    "back" parser/back!
    "login" #(lib/change-view-next! :login)
    "restart" comlib/init!))

(s/fdef dispatch-link-destination-next
        :args (s/cat :url string?)
        :ret fn?)

(defui BubbleView
  "Generate a bubble."
  static om/IQuery
  (query [this] [:type :html :text :url])
  Object
  (render [this]
          (vlib/scroll-divs-to-bottom "bubbles")
          (let [{:keys [type url]} (om/props this)
                html-content (:html (om/props this))
                bubble-content (vlib/safe-html html-content)]
            (html [:li {:className (get-bubble-class-next type)}
                   [:div.avatar]
                   [:p.messages
                    (if (some #{url} ["back" "login" "restart"])
                      [:a {:href "javascript:void(0)"
                           :onClick (dispatch-link-destination-next url)} bubble-content]
                      bubble-content)]]))))
(def bubble-view-next (om/factory BubbleView {:keyfn :text}))

(defui BubblesView
  "Generate all bubbles based on the data in the reconciler."
  static om/IQuery
  (query [this]
         `[{:discussion/bubbles ~(om/get-query BubbleView)}])
  Object
  (render [this]
          (let [{:keys [discussion/bubbles]} (om/props this)]
            (html [:ol {:className (lib/prefix-name "bubbles")}
                   (map bubble-view-next bubbles)]))))
(def bubbles-view-next (om/factory BubblesView))
