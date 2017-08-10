# discuss-cljs

Development [![build status](https://gitlab.cs.uni-duesseldorf.de/project/discuss/badges/develop/build.svg)](https://gitlab.cs.uni-duesseldorf.de/project/peerfact/commits/develop)
Master [![build status](https://gitlab.cs.uni-duesseldorf.de/project/discuss/badges/master/build.svg)](https://gitlab.cs.uni-duesseldorf.de/project/peerfact/commits/master)

Minimal front-end to include dialog-based discussion systems into existing
websites. Compatible
to [D-BAS](https://github.com/hhucn/dbas).


## Setup

Pull all bower components with:

    bower install

To get an interactive development environment run:

    rlwrap lein figwheel

and open your browser at [localhost:3449](http://localhost:3449).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

### Testing and Development

#### Property-based tests

Some property-based tests have been added. These tests and additionally all
other tests can be executed with the phantomjs runner, which has been packed
into a separate leiningen task:

    lein phantomtest

## License

Copyright © 2016-2017 Christian Meter

Distributed under the [MIT License](LICENSE).
