(ns discuss.specs
  (:require [cljs.spec.alpha :as s]
            [discuss.translations]))

(s/def ::options (s/or :option keyword?
                       :option nil?))

(s/fdef discuss.translations/translate
        :args (s/cat :group keyword?
                     :key keyword?
                     :option (s/? ::options))
        :ret string?)

(comment
  (require '[clojure.spec.test.alpha :as stest])
  (stest/instrument 'discuss.translations/translate)
  (s/exercise-fn 'discuss.translations/translate)
  (stest/check `discuss.translations/translate))
