(ns discuss.eden.ajax
  (:require [discuss.utils.common :as lib]
            [discuss.config :as config]
            [discuss.communication.lib :as comlib]
            [ajax.core :refer [GET]]
            [cljs.spec.alpha :as s]
            [discuss.utils.logging :as log]
            [discuss.eden.specs :as especs]))

(defn after-eden-post-handler []
  (lib/change-view-next! :eden/overview))

(defn post-eden-argument
  "Post an Argument directly to EDEN for later usage."
  [{:keys [premise conclusion reference search/selected]}]
  (let [request-url (str (lib/host-eden) (:add/argument config/eden))
        headers {"Content-Type" "application/json"}
        handler after-eden-post-handler
        author-id (lib/get-user-id)
        body {:premise premise
              :conclusion (if (nil? selected) conclusion (:text selected))
              :author-id author-id
              :reference reference
              :link-type :support}]
    (comlib/do-post request-url body handler comlib/error-handler headers)))

(defn- received-arguments-handler [response]
  (let [arguments (-> response lib/json->clj :arguments)
        valid-arguments (filter #(s/valid? ::especs/argument %) arguments)]
    (log/debug "Received" (count valid-arguments) "argument(s)")
    (lib/store-to-app-state! 'eden/arguments arguments)))

(defn search-arguments-by-author
  "Send current author-name to EDEN instance and return all associated arguments.
  When no author is provided, use the currently logged in user."
  ([author]
   (log/debug "Querying arguments from EDEN instance for " author)
   (GET (str (lib/host-eden) (:search/arguments-by-author config/eden))
        {:handler received-arguments-handler
         :params {:author-name author}}))
  ([] (search-arguments-by-author (lib/get-nickname))))


(search-arguments-by-author "woot")
