(ns discuss.communication.main
  "Functions concerning the communication with the remote discussion system."
  (:require [ajax.core :refer [GET POST]]
            [discuss.utils.common :as lib]
            [discuss.references.integration :as rint]
            [discuss.communication.lib :as comlib]
            [discuss.components.search.statements :as search]
            [discuss.utils.logging :as log]
            [discuss.config :as config]))

;; Handler
(defn process-url-handler
  "React on response after sending a new statement. Reset atom and call newly
  received url."
  [response]
  (search/remove-search-results!)
  (lib/hide-add-form!)
  (rint/request-references)
  (comlib/process-and-set-items-and-bubbles response))


;;;; POST functions
(defn post-json
  "Wrapper to prepare a POST request. Sending and receiving JSON."
  ([url body handler headers]
   (comlib/do-post (comlib/make-url url) body handler comlib/error-handler headers))
  ([url body handler]
   (post-json url body handler {"Content-Type" "application/json"}))
  ([url body]
   (post-json url body process-url-handler {"Content-Type" "application/json"})))

(defn- build-origin-body
  "Transform search results so that D-BAS can handle it."
  [search-result]
  (when-not (nil? search-result)
    (let [identifier (:identifier search-result)]
      {:entity-id (:entity-id identifier)
       :aggregate-id (:aggregate-id identifier)
       :author (get-in search-result [:author :nickname])
       :version (:version identifier)})))

(defn post-statement
  "Takes statement, an optional reference and maybe a search-result from EDEN to
  post it to the backend."
  [{:keys [statement reference search/selected]}]
  (if (seq (lib/get-last-api))
    (let [url (lib/get-last-api)
          headers (merge {"Content-Type" "application/json"} (comlib/token-header))
          body {:reason (if (nil? selected) statement (:text selected))
                :reference reference
                :origin (build-origin-body selected)}]
      (post-json url body process-url-handler headers))
    (log/error ":api/last-call is empty, cannot post statement to empty URL.")))

(defn post-position
  "Add new position, its reason and an optional reference to post it to the
  backend."
  [{:keys [position reason reference search/selected]}]
  (if (seq (lib/get-last-api))
    (let [url (lib/get-last-api)
          headers (merge {"Content-Type" "application/json"} (comlib/token-header))
          body {:position position
                :reason (if (nil? selected) reason (:text selected))
                :reference reference
                :origin (build-origin-body selected)}]
      (post-json url body process-url-handler headers))
    (log/error ":api/last-call is empty, cannot post statement to empty URL.")))


;;;; Get things started!
(defn init-with-references!
  "Load discussion and initially get reference to include them in the discussion."
  []
  (rint/request-references)
  (comlib/init!))
