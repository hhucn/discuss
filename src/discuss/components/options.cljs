(ns discuss.components.options
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om.next :as nom :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.bootstrap :as bs]
            [discuss.translations :as translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn- option-row
  "Generic row for multiple settings."
  {:deprecated 0.4}
  [description content]
  (dom/div #js {:className "text-center"}
           (dom/div nil description)
           (dom/div nil content)))

(defn- language
  "Component for a single language selection."
  {:deprecated 0.4}
  [language]
  (reify
    om/IRender
    (render [_]
      (dom/span nil
                (bs/button-default-sm #(lib/language! (first language)) (second language))
                " "))))

(defn- language-row
  "Language Chooser."
  {:deprecated 0.4}
  []
  (option-row (dom/span nil (vlib/fa-icon "fa-flag") (translate :options :lang :space))
              (apply dom/div nil
                     (map #(om/build language %) translations/available))))

(defn view
  "Main view for options."
  {:deprecated 0.4}
  []
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (vlib/view-header (translate :options :heading))
               (language-row)))))

;; -----------------------------------------------------------------------------
;; om.next

(defn- language-button
  "Create button to set language."
  [[lang-keyword lang-verbose]]
  (bs/button-default-sm #(lib/language! lang-keyword) lang-verbose))

(defui Options
  Object
  (render [this]
          (html [:div (vlib/view-header (translate :options :heading))
                 [:div (vlib/fa-icon "fa-flag") (translate :options :lang :space)]
                 (interpose " " (mapv language-button translations/available))])))
(def options (nom/factory Options))
