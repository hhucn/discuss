(defproject discuss "0.16.3"
  :description "Embedding dialog-based discussions into arbitrary web-contexts"
  :url "https://discuss.cs.uni-duesseldorf.de"
  :license {:name "MIT"
            :url "https://choosealicense.com/licenses/mit/"}

  :min-lein-version "2.5.3"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async "0.4.500" :exclusions [org.clojure/tools.reader]]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.reader "1.3.2"]
                 [cljsjs/react "16.8.6-0"]
                 [cljsjs/react-dom "16.8.6-0"]
                 [cljsjs/create-react-class "15.6.3-1"]
                 [org.omcljs/om "1.0.0-beta4"]
                 [com.cognitect/transit-cljs "0.8.256"]
                 [com.bhauman/figwheel-main "0.2.1"]
                 [com.bhauman/rebel-readline-cljs "0.1.4"]
                 [com.bhauman/cljs-test-display "0.1.1"]
                 [com.velisco/strgen "0.1.8"]
                 [com.cemerick/url "0.1.1"]
                 [hickory "0.7.1"]
                 [spec-provider "0.4.14"]
                 [cljs-ajax "0.8.0"]
                 [devcards "0.2.6"]
                 [sablono "0.8.6"]
                 [inflections "0.13.2"]]

  :plugins [[lein-ancient "0.6.10"]
            [lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]
            [lein-codox "0.10.6"]
            [lein-kibit "0.1.6"]]

  :source-paths ["src"]

  :local-repo ".m2/repo"

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main" "--build" "dev" "--repl"]
            "fig:test" ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "discuss.test-runner"]}
  ;; "fig:test" ["trampoline" "run" "-m" "figwheel.main" "-m" "discuss.test-runner"]


  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [nrepl "0.6.0"]
                                  [cider/piggieback "0.4.1"]]
                   :source-paths ["src" "script" "test"]
                   :resource-paths ["target"]
                   :repl-options {:init (set! *print-length* 50)
                                  :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}}

  :cljsbuild {:builds
              [{:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/discuss.js"
                           :output-dir "resources/public/js/compiled/min/out"
                           :main discuss.core
                           :preloads [discuss.utils.extensions]
                           :optimizations :simple
                           :closure-defines {discuss.config/version ~(->> (slurp "project.clj")
                                                                       (re-seq #"\".*\"")
                                                                       (first))
                                             discuss.config/build-commit ~(if (.exists (clojure.java.io/as-file ".git/ORIG_HEAD"))
                                                                            (subs (slurp ".git/ORIG_HEAD") 0 6)
                                                                            "dev")}
                           :parallel-build true
                           :compiler-stats true
                           :pretty-print false}}]}
  :figwheel {:nrepl-port 7888
             :css-dirs ["resources/public/css"]}            ;; watch and update CSS

  ;; For documentation
  :codox {:language :clojurescript
          :metadata {:doc/format :markdown}
          :source-paths ["src/discuss"]
          :source-uri "https://gitlab.cs.uni-duesseldorf.de/cn-tsn/project/discuss/blob/master/{filepath}#L{line}"
          :doc-paths ["docs"]
          :output-path "target/docs"})
