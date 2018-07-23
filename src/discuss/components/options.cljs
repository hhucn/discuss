(ns discuss.components.options
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.bootstrap :as bs]
            [discuss.translations :as translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn- language-button
  "Create button to set language."
  [[lang-keyword lang-verbose]]
  (bs/button-default-sm #(lib/language-next! lang-keyword) lang-verbose))

(defui Options
  Object
  (render [this]
          (html [:div (vlib/view-header (translate :options :heading))
                 [:div (vlib/fa-icon "fa-flag") (translate :options :lang :space)]
                 (interpose " " (mapv language-button translations/available))])))
(def options (om/factory Options))
