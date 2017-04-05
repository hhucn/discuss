(ns discuss.utils.specs
  (:require [cljs.spec :as s]
            [clojure.spec.test :as stest]))

(s/fdef discuss.utils.common/trim-and-normalize
        :args (s/cat :str string?)
        :ret string?
        :fn #(<= (-> % :ret count) (-> % :args :str count)))

;; (stest/instrument 'discuss.utils.common/trim-and-normalize)
;; (s/exercise-fn `discuss.utils.common/trim-and-normalize)
;; (stest/abbrev-result (first (stest/check `discuss.utils.common/trim-and-normalize)))
