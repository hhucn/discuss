(ns discuss.utils.specs
  (:require [cljs.spec :as s]
            [clojure.spec.test :as stest]))

(s/fdef discuss.utils.common/trim-all
        :args (s/cat :str string?)
        :ret string?
        :fn #(<= (:ret %) (-> % :args :str)))

(stest/instrument `discuss.utils.common/trim-all)
;; (s/exercise-fn `discuss.utils.common/trim-all)
;; (stest/abbrev-result (first (stest/check `discuss.utils.common/trim-all)))
