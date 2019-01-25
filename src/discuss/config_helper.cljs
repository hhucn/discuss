(ns discuss.config-helper
  (:require [clojure.string :as string]
            [cljs.spec.alpha :as s]
            [cemerick.url :as url]))

(defn- prepend-slash
  "Prepend slash to string if none found."
  [s]
  (if (string/starts-with? s "/") s (str "/" s)))
(s/fdef prepend-slash
  :args (s/cat :s string?)
  :ret string?)

(defn- parse-query-parameter
  "Provide a key which should be parsed from the URL. Return nil if key was not
  found."
  [query-parameter]
  (get (:query (url/url (-> js/window .-location .-href)))
       query-parameter))
(s/fdef parse-query-parameter
  :args (s/cat :query-parameter string?)
  :ret (s/? string?))

(defn default-slug
  "The default slug is either provided by a query-parameter `slug` or uses the
  first slug from the available discussions in `initial-discussions`."
  [initial-discussions]
  (prepend-slash
   (or (parse-query-parameter "slug") (:slug (first initial-discussions)))))
(s/fdef default-slug
  :ret string?)

