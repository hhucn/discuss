(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.views :as views]))

(enable-console-print!)

(def app-state
  (atom {:title "discuss"
         :intro "The current discussion is about"
         :discussion
                {:heading {:intro  "What is the initial position you are interested in?"
                           :outro  ""
                           :bridge ""}}

         :issue
                {:uid  1
                 :date "12:41 PM, 24. Feb. 2016"
                 :info "Your familiy argues about whether to buy a cat or dog as pet. Now your opinion matters!",
                 :slug "cat-or-dog",
                 :title "Cat or Dog",
                 :arg_count 99}

         :items
                [{:id       "item_1"
                  :title    "We should get a cat"
                  :url      "api/cat-or-dog/attitude/1"
                  :attitude "start"}
                 {:id       "item_2"
                  :title    "We should get a dog"
                  :url      "api/cat-or-dog/attitude/2"
                  :attitude "start"}
                 {:id       "item_3"
                  :title    "We could get both, a cat and a dog"
                  :url      "api/cat-or-dog/attitude/3"
                  :attitude "start"}]}))


(defn display [show]
  (if show
    #js {}
    #js {:display "none"}))


(defn handle-change [e text owner]
  (om/transact! text (fn [_] (.. e -target -value))))

(defn commit-change [text owner]
  (om/set-state! owner :editing false))


(defn editable [text owner]
  (reify
    om/IInitState
    (init-state [_]
      {:editing false})
    om/IRenderState
    (render-state [_ {:keys [editing]}]
      (dom/li nil
              (dom/span #js {:style (display (not editing))} (om/value text))
              (dom/input
                #js {:style (display editing)
                     :value (om/value text)
                     :onChange #(handle-change % text owner)
                     :onKeyDown #(when (= (.-key %) "Enter")
                                  (commit-change text owner))
                     :onBlur (fn [e] (commit-change text owner))})
              (dom/button
                #js {:style (display (not editing))
                     :onClick #(om/set-state! owner :editing true)}
                "Edit")))))


(defn people [data]
  (->> data
       :people
       (mapv (fn [x]
               (if (:classes x)
                 (update-in x [:classes]
                            (fn [cs] (mapv (:classes data) cs)))
                 x)))))

;; Register
(om/root views/main-view app-state
         {:target (. js/document (getElementById "discuss-main"))})

(om/root views/clipboard-view app-state
         {:target (. js/document (getElementById "discuss-clipboard"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
