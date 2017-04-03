(ns discuss.components.options
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.utils.bootstrap :as bs]
            [discuss.translations :as translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn- option-row
  "Generic row for multiple settings."
  [description content]
  (dom/div #js {:className "text-center"}
           (dom/div nil description)
           (dom/div nil content)))

(defn- language
  "Component for a single language selection."
  [language]
  (reify
    om/IRender
    (render [_]
      (dom/span nil
                (bs/button-default-sm #(lib/language! (first language)) (second language))
                " "))))

(defn- language-row
  "Language Chooser."
  []
  (option-row (dom/span nil (vlib/fa-icon "fa-flag") (translate :options :lang :space))
              (apply dom/div nil
                     (map #(om/build language %) translations/available))))

(defn view
  "Main view for options."
  []
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (vlib/view-header (translate :options :heading))
               (language-row)))))
