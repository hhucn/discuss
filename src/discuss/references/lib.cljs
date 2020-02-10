(ns discuss.references.lib
  (:require [discuss.utils.common :as lib]))

(defn split-at-string
  "Split the input-string at the position of the query-string.
  Nearly the same as clojure.string/split. Not based on a regex, but instead
  based on a string to match strings containing literals, which could be
  special-characters in a regex.

  Examples:
  (split-at-string \"barfoo?baz\" \"foo?\") => [\"bar\" \"baz\"]

  (split-at-string \"abc\" \"def\") => [\"abc\"]

  (split-at-string \"0\" \"0\") => []"
  [s query]
  (when-not (some nil? [s query])
    (let [idx (.indexOf s query)
          left (subs s 0 idx)
          right (subs s (+ (count query) idx) (inc (count s)))]
      (cond
        (or (neg? idx) (empty? query)) [s]
        (or (empty? s)) [""]
        (= s query) []
        :else [left right]))))

(defn save-selected-reference!
  "Saves the currently clicked reference for further processing."
  [ref]
  (lib/store-to-app-state! 'references/selected ref))

(defn remove-selected-reference!
  "Remove the previously selected reference."
  []
  (lib/store-to-app-state! 'references/selected nil))

(defn get-selected-reference
  "Returns the currently selected reference."
  []
  (lib/load-from-app-state :references/selected))

(defn save-reference-usages!
  "Saves the currently selected statement for further processing."
  [references]
  (lib/store-to-app-state! 'references/usages references))

(defn get-reference-usages
  "Return list of reference usages, which were previously stored in the app-state."
  []
  (lib/load-from-app-state :references/usages))


;;;; Maintain list of already seen references
(defn get-highlighted
  "Return collection with the already highlighted references."
  []
  (lib/load-from-app-state :references/highlighted))

(defn highlighted?
  "See if ref is already in the list of the already highlighted references."
  [ref]
  (contains? (get-highlighted) ref))

(defn highlight!
  "Add ref to collection of already highlighted references."
  [ref]
  (lib/store-to-app-state! 'references/highlighted (conj (get-highlighted) ref)))
