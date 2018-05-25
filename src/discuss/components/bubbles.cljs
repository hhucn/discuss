(ns discuss.components.bubbles
  (:require [om.core :as om]
            [om.dom :as dom]
            [om.next :as nom :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [discuss.history :as history]
            [discuss.utils.common :as lib]
            [discuss.communication.lib :as comlib]
            [discuss.utils.views :as vlib]))

(defn- get-bubble-class [bubble]
  "Check bubble type and return a class-string to match the CSS styles."
  {:deprecated 0.4
   :alternative 'get-bubble-class-next}
  (cond
    (:is_user bubble) "bubble-user"
    (:is_system bubble) "bubble-system"
    (:is_status bubble) "bubble-status text-center"
    (:is_info bubble) "bubble-info text-center"))

(defn- dispatch-link-destination
  "Dispatch which link should be set."
  [anchor]
  (let [data-href (.getAttribute anchor "data-href")]
    (case data-href
      "back" history/back!
      "login" #(lib/change-view! :login)
      "restart" comlib/init!)))

(defn- convert-link
  "Given a DOM element, search for anchor-children and correctly set onClick and href properties."
  [dom-node]
  (let [children (gdom/getChildren dom-node)
        anchors (filter #(= "a" (string/lower-case (.-nodeName %))) children)]
    (when (pos? (count anchors))
      (doall (map (fn [anchor]
                    (set! (.-href anchor) "javascript:void(0)")
                    (set! (.-onclick anchor) (dispatch-link-destination anchor)))
                  anchors)))))

(defn convert-links-in-bubbles
  "Reads data attributes and set correct links."
  []
  (let [messages (gdom/getElementsByClass (lib/prefix-name "converted-bubbles"))]
    (doall (map convert-link messages))))

(defn bubble-view [bubble]
  (reify
    om/IWillUpdate
    (will-update [_ _ _]
      (vlib/scroll-divs-to-bottom "bubbles")
      (convert-links-in-bubbles))
    om/IRender
    (render [_]
      (let [bubble-class (get-bubble-class bubble)]
        (vlib/scroll-divs-to-bottom "bubbles")
        (convert-links-in-bubbles)
        (dom/li #js {:className bubble-class}
                (dom/div #js {:className "avatar"})
                (dom/p #js {:className "messages"}
                       (vlib/safe-html (:message bubble))))))))

(defn view []
  (reify
    om/IWillUpdate
    (will-update [_ _ _]
      (vlib/scroll-divs-to-bottom "bubbles"))
    om/IRender
    (render [_]
      (apply dom/ol #js {:className (lib/prefix-name "bubbles")}
             (map #(om/build bubble-view (lib/merge-react-key %)) (lib/get-bubbles))))))


;; -----------------------------------------------------------------------------
;; om.next

(defn- get-bubble-class-next [bubble-type]
  "Check bubble type and return a class-string to match the CSS styles."
  (case bubble-type
    :user "bubble-user"
    :system "bubble-system"
    :status "bubble-status text-center"
    :info "bubble-info text-center"))

(get-bubble-class-next :system)

(defui BubbleView
  "TODO: do something with URL"
  static nom/IQuery
  (query [this] [:type :html :text :url])
  Object
  (render [this]
          (vlib/scroll-divs-to-bottom "bubbles")
          #_(convert-links-in-bubbles)
          (let [{:keys [type text url]} (nom/props this)
                html-content (:html (nom/props this))]
            (html [:li {:className (get-bubble-class-next type)}
                   [:div.avatar]
                   [:p.messages

                    (vlib/safe-html html-content)]]))))
(def bubble-view-next (nom/factory BubbleView))

(defui BubblesView
  static nom/IQuery
  (query [this]
         `[{:discussion/bubbles ~(nom/get-query BubbleView)}])
  Object
  (render [this]
          (let [{:keys [discussion/bubbles]} (nom/props this)]
            (html [:div (map bubble-view-next bubbles)]))))
(def bubbles-view-next (nom/factory BubblesView))
