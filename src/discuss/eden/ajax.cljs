(ns discuss.eden.ajax
  (:require [discuss.utils.common :as lib]
            [discuss.config :as config]
            [discuss.communication.lib :as comlib]))

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
