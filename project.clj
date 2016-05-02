(defproject discuss "0.1"
  :description ""
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.3"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.34"]
                 [org.clojure/core.async "0.2.374"
                  :exclusions [org.clojure/tools.reader]]
                 [org.omcljs/om "1.0.0-alpha32"]
                 [cljs-ajax "0.5.4"]                        ; AJAX for om
                 [com.cognitect/transit-cljs "0.8.237"]     ; Better JSON support
                 [figwheel-sidecar "0.5.2"]]

  :plugins [[lein-figwheel "0.5.1"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]
            [lein-codox "0.9.4"]
            [lein-ancient "0.6.8"]
            [lein-kibit "0.1.2"]]

  :source-paths ["src/discuss" "src/test"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]

                ;; If no code is to be run, set :figwheel true for continued automagical reloading
                :figwheel {                                 ;:load-warninged-code true
                           :on-jsload "discuss.core/on-js-reload"}

                :compiler {:main discuss.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/discuss.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/discuss.js"
                           :main discuss.core
                           :optimizations :advanced
                           :pretty-print false}}
               {:id "test"
                :source-paths ["src" "test"]
                :notify-command ["phantomjs" "resources/test/phantom/runner.js" "resources/public/js/compiled/discuss-test.js"]
                :compiler {:output-to "resources/public/js/compiled/discuss-test.js"
                           :optimizations :whitespace
                           :pretty-print true}}]
              :test-commands {"unit" ["phantomjs"
                                      "resources/test/phantom/runner.js"
                                      "resources/test/test.html"]}}

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
  :codox {:language :clojurescript
          :metadata {:doc/format :markdown}
          :source-uri "https://gitlab.cs.uni-duesseldorf.de/project/discuss/blob/master/{filepath}#L{line}"
          :doc-paths ["docs"]
          :output-path "target/docs"}
  )
