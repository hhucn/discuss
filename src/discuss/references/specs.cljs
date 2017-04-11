(ns discuss.references.specs
  (:require [cljs.spec :as s]
            [clojure.spec.test :as stest]
            #_[discuss.references.lib]))

(s/fdef discuss.references.lib/split-at-string
        :args (s/cat :body string?
                     :query string?)
        :ret (s/cat :first string?
                    :second (s/? string?))
        :fn #(<= 1 (-> % :ret count) 2))

;; (stest/instrument 'discuss.references.lib/split-at-string)
;; (s/exercise-fn `discuss.references.lib/split-at-string)
