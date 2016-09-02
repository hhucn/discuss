(defproject discuss "0.1.375"
  :description ""
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.3"

  :hooks [leiningen.cljsbuild]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.93"]
                 [org.clojure/core.async "0.2.385" :exclusions [org.clojure/tools.reader]]
                 [org.omcljs/om "1.0.0-alpha36"]
                 [cljs-ajax "0.5.8"]                        ; AJAX for om
                 [com.cognitect/transit-cljs "0.8.239"]     ; Better JSON support
                 [figwheel-sidecar "0.5.3-2" :scope "devcards"]
                 [devcards "0.2.1-7" :scope "devcards" :exclusions [org.clojure/clojurescript]]]

  :plugins [[lein-figwheel "0.5.3-2"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]
            [lein-codox "0.9.5"]
            [lein-ancient "0.6.10"]
            [lein-kibit "0.1.2"]]

<<<<<<< Updated upstream
  :source-paths ["src/discuss"]
=======
  :source-paths ["src/discuss" "src/test" "script"]
>>>>>>> Stashed changes

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
<<<<<<< Updated upstream
              [{:id "dev"
                :source-paths ["src/discuss"]
                :figwheel {:on-jsload "discuss.core/on-js-reload"}
                :compiler {:main discuss.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/discuss.js"
                           :output-dir "resources/public/js/compiled/out"
                           :parallel-build true
                           :compiler-stats true
                           :source-map-timestamp true}}
               {:id "devcards"
                :source-paths ["src/discuss" "src/devcards"]
                :figwheel {:devcards true}
                :compiler {:main discuss.devcards.core
                           :asset-path "../js/compiled/devcards/out"
                           :output-to "resources/public/js/compiled/devcards/discuss.js"
                           :output-dir "resources/public/js/compiled/devcards/out"
                           :parallel-build true
                           :compiler-stats true
                           :source-map-timestamp true}}
               {:id "test"
                :source-paths ["src/discuss" "src/devcards"]
                :compiler {:main discuss.devcards.test
                           :asset-path "../js/compiled/test/out"
                           :output-to "resources/public/js/compiled/test/discuss.js"
                           :output-dir "resources/public/js/compiled/test/out"
                           :parallel-build true
                           :compiler-stats true
                           :source-map-timestamp true}}
               {:id "min"
=======
              [{:id           "dev"
                :source-paths ["src/discuss" "src/test" "script"]
                :figwheel     {:on-jsload "discuss.core/on-js-reload"}
                :compiler     {:main                 discuss.core
                               :preloads             [discuss.utils.extensions devtools.preload]
                               :asset-path           "js/compiled/out"
                               :output-to            "resources/public/js/compiled/discuss.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :parallel-build       true
                               :compiler-stats       true
                               :source-map-timestamp true}}
               {:id           "devcards"
                :source-paths ["src/discuss" "src/devcards" "src/test" "script"]
                :figwheel     {:devcards true}
                :compiler     {:main                 discuss.devcards.core
                               :preloads             [discuss.utils.extensions devtools.preload]
                               :asset-path           "../js/compiled/devcards/out"
                               :output-to            "resources/public/js/compiled/discuss.js"
                               :output-dir           "resources/public/js/compiled/devcards/out"
                               :parallel-build       true
                               :compiler-stats       true
                               :source-map-timestamp true}}
               {:id           "test"
                :source-paths ["src/discuss" "src/devcards" "src/test" "script"]
                :compiler     {:output-to     "resources/public/js/testable.js"
                               :output-dir    "resources/public/js/compiled/test/out"
                               :main          discuss.test.runner
                               :preloads      [discuss.utils.extensions]
                               :optimizations :none}}
               {:id           "min"
>>>>>>> Stashed changes
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/discuss.js"
                           :main discuss.core
                           :optimizations :advanced
                           :parallel-build true
                           :compiler-stats true
                           :pretty-print false}}]}
  :figwheel {;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             }

  ;; For documentation
  :codox {:language    :clojurescript
          :metadata    {:doc/format :markdown}
          :source-uri  "https://gitlab.cs.uni-duesseldorf.de/project/discuss/blob/master/{filepath}#L{line}"
          :doc-paths   ["docs"]
          :output-path "target/docs"}
  )
