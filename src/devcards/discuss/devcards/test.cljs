 (ns discuss.devcards.test
   (:require [discuss.devcards.core]
             [discuss.devcards.lib]))

 (defn -main [& args]
   (println "Hello world!")
   (cljs.test/run-all-tests))

(set! *main-cli-fn* -main)