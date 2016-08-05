(ns discuss.components.contribute
  (:require [om.dom :as dom]
            [om.core :as om]
            [discuss.utils.views :as vlib]
            [discuss.utils.common :as lib]))

(defn view
  "Add panel with options for contribution."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "contribute-wrapper")}
               (dom/a #js {:href "https://gitlab.cs.uni-duesseldorf.de/project/discuss/issues"
                           :target "_blank"
                           :className (lib/prefix-name "buttons")}
                        (vlib/fa-icon "fa-exclamation-circle")
                        "Problem")))))