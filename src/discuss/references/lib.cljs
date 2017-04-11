(ns discuss.references.lib
  (:require [discuss.utils.common :as lib]
            [om.dom :as dom]
            [om.core :as om]))

(defn split-at-string
  "Split the input-string at the position of the query-string.
  Nearly the same as clojure.string/split. Not based on a regex, but instead
  based on a string to match strings containing literals, which could be
  special-characters in a regex.

  Examples:
  (split-at-string \"barfoo?baz\" \"foo?\")
  => [\"bar\" \"baz\"]
  (split-at-string \"abc\" \"def\")
  => [\"abc\"]
  (split-at-string \"0\" \"0\")
  => []"
  [s query]
  (when-not (some nil? [s query])
    (let [idx (.indexOf s query)
          left (subs s 0 idx)
          right (subs s (+ (count query) idx) (inc (count s)))]
      (cond
        (or (neg? idx) (empty? query)) [s]
        (or (empty? s)) [""]
        (= s query) []
        :default [left right]))))

(defn save-selected-reference!
  "Saves the currently clicked reference for further processing."
  [ref]
  (lib/update-state-item! :references :selected (fn [_] ref)))

(defn remove-selected-reference!
  "Remove the previously selected reference."
  []
  (lib/update-state-item! :references :selected (fn [_] nil)))

(defn get-selected-reference
  "Returns the currently selected reference."
  []
  (get-in @lib/app-state [:references :selected]))

(defn save-reference-usages!
  "Saves the currently selected statement for further processing."
  [references]
  (lib/update-state-item! :references :usages (fn [_] references)))

(defn get-reference-usages
  "Return list of reference usages, which were previously stored in the app-state."
  []
  (get-in @lib/app-state [:references :usages]))


;;;; Maintain list of already seen references
(defn get-highlighted
  "Return collection with the already highlighted references."
  []
  (get-in @lib/app-state [:references :highlighted]))

(defn highlighted?
  "See if ref is already in the list of the already highlighted references."
  [ref]
  (contains? (get-highlighted) ref))

(defn highlight!
  "Add ref to collection of already highlighted references."
  [ref]
  (lib/update-state-item! :references :highlighted (fn [_] (conj (get-highlighted) ref))))


;;;; View Components
(defn- current-reference-component
  "Return DOM element showing which reference is currently selected."
  [_ owner]
  (reify
    om/IRender
    (render [_]
      (om/observe owner (lib/get-cursor :layout))
      (dom/div #js {:className ""
                    :style     #js {:paddingBottom "1em"}}
               (let [ref-title (:text (get-selected-reference))]
                 (dom/em nil "\"" ref-title "\""))))))
