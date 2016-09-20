(ns discuss.components.options
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn entry
  "Entrypoint to show options. Should be something like a onclick handler."
  []
  (dom/div #js {:className "text-muted"}
           (vlib/fa-icon "fa-cog" #(lib/change-view! :options))))

(defn view
  "Main view for options."
  [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (vlib/view-header (translate :common :options))))))