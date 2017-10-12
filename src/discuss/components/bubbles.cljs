(ns discuss.components.bubbles
  (:require [om.core :as om]
            [om.dom :as dom]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [discuss.history :as history]
            [discuss.utils.common :as lib]
            [discuss.communication.lib :as comlib]
            [discuss.utils.views :as vlib]))

(defn- get-bubble-class [bubble]
  "Check bubble type and return a class-string to match the CSS styles."
  (cond
    (:is_user bubble) "bubble-user"
    (:is_system bubble) "bubble-system"
    (:is_status bubble) "bubble-status text-center"
    (:is_info bubble) "bubble-info text-center"))

(defn- dispatch-link-destination
  "Dispatch which link should be set."
  [anchor]
  (let [data-href (.getAttribute anchor "data-href")]
    (cond
      (= data-href "back") history/back!
      (= data-href "login") #(lib/change-view! :login)
      (= data-href "restart") comlib/init!)))

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
