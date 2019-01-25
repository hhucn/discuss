(ns discuss.components.issue_selector
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.translations :refer [translate] :rename {translate t}]))

(defui Actual
  static om/IQuery
  (query [this]
         []))

(defui Expected
  static om/IQuery
  (query [this]
         []))
