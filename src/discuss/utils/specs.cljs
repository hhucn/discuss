(ns discuss.utils.specs
  (:require [cljs.spec :as s]
            [clojure.spec.test :as stest]))

(s/fdef discuss.utils.common/trim-all
        :args (s/cat :str string?)
        :ret string?
        :fn #(> (-> % :ret count) (-> % :args :str count)))

(stest/instrument 'discuss.utils.common/trim-all)
;; (s/exercise-fn `discuss.utils.common/trim-all)
;; (stest/abbrev-result (first (stest/check `discuss.utils.common/trim-all)))

;; (map #(-> % :clojure.test.check/ret pprint/pprint) (stest/check `discuss.utils.common/trim-all))

;; (require '[discuss.utils.common-test])
;; (discuss.utils.common-test/check' (stest/check `discuss.utils.common/trim-all))
