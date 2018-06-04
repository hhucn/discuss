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
            [discuss.utils.views :as vlib]
            [cljs.spec.alpha :as s]))

(defn- get-bubble-class
  "Check bubble type and return a class-string to match the CSS styles."
  {:deprecated 0.4
   :alternative 'get-bubble-class-next}
  [bubble]
  (cond
    (:is_user bubble) "bubble-user"
    (:is_system bubble) "bubble-system"
    (:is_status bubble) "bubble-status text-center"
    (:is_info bubble) "bubble-info text-center"))

(defn- dispatch-link-destination
  "Dispatch which link should be set."
  {:deprecated 0.4}
  [anchor]
  (let [data-href (.getAttribute anchor "data-href")]
    (case data-href
      "back" history/back!
      "login" #(lib/change-view! :login)
      "restart" comlib/init!)))

(defn- convert-link
  "Given a DOM element, search for anchor-children and correctly set onClick and href properties."
  {:deprecated 0.4}
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
  {:deprecated 0.4}
  []
  (let [messages (gdom/getElementsByClass (lib/prefix-name "converted-bubbles"))]
    (doall (map convert-link messages))))

(defn bubble-view
  {:deprecated 0.4}
  [bubble]
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

(defn view
  {:deprecated 0.4}
  []
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
    "back" history/back!
    "login" #(lib/change-view! :login)
    "restart" comlib/init!))

(s/fdef dispatch-link-destination-next
        :args (s/cat :url string?)
        :ret fn?)

(defui BubbleView
  "Generate a bubble."
  static nom/IQuery
  (query [this] [:type :html :text :url])
  Object
  (render [this]
          (vlib/scroll-divs-to-bottom "bubbles")
          (let [{:keys [type url]} (nom/props this)
                html-content (:html (nom/props this))
                bubble-content (vlib/safe-html html-content)]
            (html [:li {:className (get-bubble-class-next type)}
                   [:div.avatar]
                   [:p.messages
                    (if (some #{url} ["back" "login" "restart"])
                      [:a {:href "javascript:void(0)"
                           :onClick (dispatch-link-destination-next url)} bubble-content]
                      bubble-content)]]))))
(def bubble-view-next (nom/factory BubbleView {:keyfn :text}))

(defui BubblesView
  "Generate all bubbles based on the data in the reconciler."
  static nom/IQuery
  (query [this]
         `[{:discussion/bubbles ~(nom/get-query BubbleView)}])
  Object
  (render [this]
          (let [{:keys [discussion/bubbles]} (nom/props this)]
            (html [:ol {:className (lib/prefix-name "bubbles")}
                   (map bubble-view-next bubbles)]))))
(def bubbles-view-next (nom/factory BubblesView))
