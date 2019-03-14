(ns discuss.utils.localstorage)

(defn set-item!
  "Store `v` under `k` in localstorage."
  [k v]
  (.setItem (.-localStorage js/window) k v))

(defn get-item
  "Look up `k` in localstorage."
  [k]
  (.getItem (.-localStorage js/window) k))

(defn remove-item!
  "Remove value from localstorage at `k`."
  [k]
  (.removeItem (.-localStorage js/window) k))
