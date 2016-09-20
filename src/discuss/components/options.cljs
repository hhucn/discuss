(ns discuss.components.options
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.utils.bootstrap :as bs]
            [discuss.translations :as translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn entry
  "Entrypoint to show options. Should be something like a onclick handler."
  []
  (dom/div #js {:className "text-muted pull-right"}
           (dom/div #js {:className "pointer"
                         :onClick   #(lib/change-view! :options)}
                    (vlib/fa-icon "fa-cog")
                    " "
                    (translate :options :heading))))

(defn- option-row
  "Generic row for multiple settings."
  [description content]
  (dom/div #js {:className "row"}
           (dom/div #js {:className "col-md-offset-1 col-md-3"} description)
           (dom/div #js {:className "col-md-7"} content)))

(defn- language
  "Component for a single language selection."
  [language]
  (reify
    om/IRender
    (render [_]
      (dom/span nil
                (bs/button-default-sm #(lib/language! (first language)) (second language))
                " "))))

(defn- languages
  "Language Chooser."
  []
  (option-row (translate :options :lang)
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
               (languages)))))