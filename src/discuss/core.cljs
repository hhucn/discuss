(ns ^:figwheel-always discuss.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.views :as views]))

(enable-console-print!)

(extend-type js/String
  ICloneable
  (-clone [s] (js/String. s))
  om/IValue
  (-value [s] (str s)))

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
                  :attitude "start"}]

         :people
                [{:type :student :first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
                 {:type :student :first "Alyssa" :middle-initial "P" :last "Hacker"
                  :email "aphacker@mit.edu"}
                 {:type :professor :first "Gerald" :middle "Jay" :last "Sussman"
                  :email "metacirc@mit.edu" :classes [:6001 :6946]}
                 {:type :student :first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
                 {:type :student :first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
                 {:type :professor :first "Hal" :last "Abelson" :email "evalapply@mit.edu"
                  :classes [:6001]}]
         :classes
                {:6001 "The Structure and Interpretation of Computer Programs"
                 :6946 "The Structure and Interpretation of Classical Mechanics"
                 :1806 "Linear Algebra"}}))

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

(defn registry-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id "registry"}
               (dom/h2 nil "Registry")
               (apply dom/ul nil
                      (om/build-all views/entry-view (people data)))))))

(defn classes-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id "classes"}
               (dom/h2 nil "Classes")
               (apply dom/ul nil
                      (om/build-all editable (vals (:classes data))))))))


;; Register
(om/root views/main-view app-state
         {:target (. js/document (getElementById "discuss-main"))})

(om/root views/clipboard-view app-state
         {:target (. js/document (getElementById "discuss-clipboard"))})

(om/root registry-view app-state
         {:target (. js/document (getElementById "registry"))})

(om/root classes-view app-state
         {:target (. js/document (getElementById "classes"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
