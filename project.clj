(defproject discuss "0.2.1"
  :description ""
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.3"

  :hooks [leiningen.cljsbuild]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.473"]
                 [org.clojure/core.async "0.2.395" :exclusions [org.clojure/tools.reader]]
                 [org.clojure/test.check "0.9.0"]
                 [org.omcljs/om "1.0.0-alpha47"]
                 [cljs-ajax "0.5.8"]                        ; AJAX for om
                 [com.cognitect/transit-cljs "0.8.239"]     ; Better JSON support
                 [inflections "0.13.0"]
                 [lein-doo "0.1.7"]]         ; local storage support

  :plugins [[lein-figwheel "0.5.9"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]
            [lein-codox "0.9.5"]
            [lein-ancient "0.6.10"]
            [lein-kibit "0.1.2"]
            [lein-doo "0.1.7"]]

  :source-paths ["src/discuss" "src/test" "script"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :aliases {"phantomtest" ["do" "clean" ["doo" "phantom" "test" "once"]]
            "build" ["do" "clean" ["cljsbuild" "once" "min"]]}

  ;; setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.1"]
                                  [figwheel-sidecar "0.5.9"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src/discuss" "src/test"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/discuss" "src/test" "script"]
                :figwheel     {:on-jsload "discuss.core/on-js-reload"}
                :compiler     {:main                 discuss.core
                               :preloads             [discuss.utils.extensions devtools.preload]
                               :asset-path           "js/compiled/out"
                               :output-to            "resources/public/js/compiled/discuss.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :closure-defines {discuss.config/version ~(->> (slurp "project.clj")
                                                                              (re-seq #"\".*\"")
                                                                              (first))}
                               :parallel-build       true
                               :compiler-stats       true
                               :source-map-timestamp true}}
               {:id           "test"
                :source-paths ["src/discuss" "src/test" "script"]
                :compiler     {:output-to     "resources/public/js/compiled/testable.js"
                               :output-dir    "resources/public/js/compiled/test/out"
                               :main          discuss.tests
                               ;:preloads      [discuss.utils.extensions]
                               :optimizations :none}}
               {:id           "min"
                :source-paths ["src"]
                :compiler     {:output-to      "resources/public/js/compiled/discuss.js"
                               :output-dir     "resources/public/js/compiled/min/out"
                               :main           discuss.core
                               :preloads       [discuss.utils.extensions]
                               :optimizations  :advanced
                               :closure-defines {discuss.config/remote-host ~(or (System/getenv "REMOTE_HOST") "/")}
                               :parallel-build true
                               :compiler-stats true
                               :pretty-print   false}}]}
  :figwheel {:css-dirs ["resources/public/css"]}             ;; watch and update CSS

  ;; For documentation
  :codox {:language    :clojurescript
          :metadata    {:doc/format :markdown}
          :source-uri  "https://gitlab.cs.uni-duesseldorf.de/project/discuss/blob/master/{filepath}#L{line}"
          :doc-paths   ["docs"]
          :output-path "target/docs"}
  )
