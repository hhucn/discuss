(ns discuss.utils.localstorage
  "Code taken from https://gist.github.com/daveliepmann/cf923140702c8b1de301,
   thanks to @daveliepmann!")

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
