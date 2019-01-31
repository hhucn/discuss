(defproject discuss "0.5.0"
  :description "Embedding dialog-based discussions into arbitrary web-contexts"
  :url "https://discuss.cs.uni-duesseldorf.de"
  :license {:name "MIT"
            :url  "https://choosealicense.com/licenses/mit/"}

  :min-lein-version "2.5.3"

  ;; :hooks [leiningen.cljsbuild]

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 [org.clojure/core.async "0.4.490" :exclusions [org.clojure/tools.reader]]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.reader "1.3.2"]
                 [org.omcljs/om "1.0.0-beta4"]
                 [com.cognitect/transit-cljs "0.8.256"]
                 [com.velisco/strgen "0.1.7"]
                 [com.cemerick/url "0.1.1"]
                 [spec-provider "0.4.14"]
                 [cljs-ajax "0.8.0"]
                 [lein-doo "0.1.11"]  ;; <-- otherwise it won't find the doo namespaces...
                 [devcards "0.2.6"]
                 [sablono "0.8.4"]
                 [inflections "0.13.0"]]

  :plugins [[lein-ancient "0.6.10"]
            [lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]
            [lein-codox "0.10.3"]
            [lein-doo "0.1.11"]
            [lein-figwheel "0.5.17"]
            [lein-kibit "0.1.6"]
            [lein-set-version "0.4.1"]]

  :source-paths ["src"]

  :local-repo ".m2/repo"

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :aliases {"phantomtest" ["do" "clean" ["doo" "phantom" "test" "once"]]
            "build" ["do" "clean" ["cljsbuild" "once" "min"]]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [figwheel-sidecar "0.5.17"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [cider/piggieback "0.3.10"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src" "script"]
                   :repl-options {:init (set! *print-length* 50)
                                  :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}}

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:devcards true
                           :open-urls ["http://localhost:3449/cards.html"]}
                :compiler {:main       devcards.discuss.core
                           :preloads   [discuss.utils.extensions devtools.preload]
                           :asset-path "js/compiled/discuss_cards_out"
                           :output-to  "resources/public/js/compiled/discuss_cards.js"
                           :output-dir "resources/public/js/compiled/discuss_cards_out"
                           :parallel-build       true
                           :compiler-stats       true
                           :source-map-timestamp true}}
               {:id           "dev-default"
                :source-paths ["src"]
                :figwheel     {:on-jsload "discuss.core/on-js-reload"
                               ;; :open-urls ["http://localhost:3449"]
                               }
                :compiler     {:main            discuss.core
                               :preloads        [discuss.utils.extensions devtools.preload]
                               :asset-path      "js/compiled/out"
                               :output-to       "resources/public/js/compiled/discuss.js"
                               :output-dir      "resources/public/js/compiled/out"
                               :closure-defines {discuss.config/version ~(->> (slurp "project.clj")
                                                                              (re-seq #"\".*\"")
                                                                              (first))}
                               :parallel-build       true
                               :compiler-stats       true
                               :source-map-timestamp true}}
               {:id           "test"
                :source-paths ["src"]
                :compiler     {:output-to "resources/public/js/compiled/testable.js"
                               :output-dir "resources/public/js/compiled/test/out"
                               :main discuss.tests
                               :process-shim false
                               ;; :preloads [discuss.utils.extensions]
                               :optimizations :none}}
               {:id           "min"
                :source-paths ["src"]
                :compiler     {:output-to      "resources/public/js/compiled/discuss.js"
                               :output-dir     "resources/public/js/compiled/min/out"
                               :main           discuss.core
                               :preloads       [discuss.utils.extensions]
                               :optimizations  :simple
                               :closure-defines {discuss.config/version ~(->> (slurp "project.clj")
                                                                              (re-seq #"\".*\"")
                                                                              (first))
                                                 discuss.config/build-commit ~(if (.exists (clojure.java.io/as-file ".git/ORIG_HEAD"))
                                                                                (subs (slurp ".git/ORIG_HEAD") 0 7)
                                                                                "dev")}
                               ;; :closure-defines {discuss.config/remote-host ~(or (System/getenv "REMOTE_HOST") "https://dbas.cs.uni-duesseldorf.de/api")}
                               :parallel-build true
                               :compiler-stats true
                               :pretty-print   false}}]}
  :figwheel {:nrepl-port 7888
             :css-dirs ["resources/public/css"]}             ;; watch and update CSS

  :jvm-opts ~(let [version (System/getProperty "java.version")
                   [major _ _] (clojure.string/split version #"\.")]
               (if (>= (Integer. major) 9)
                 ["--add-modules" "java.xml.bind"]
                 []))

  ;; For documentation
  :codox {:language    :clojurescript
          :metadata    {:doc/format :markdown}
          :source-paths ["src/discuss"]
          :source-uri "https://gitlab.cs.uni-duesseldorf.de/cn-tsn/project/discuss/blob/master/{filepath}#L{line}"
          :doc-paths ["docs"]
          :output-path "target/docs"})
