# discuss-cljs

Minimal front-end to include dialog-based discussion systems into existing
websites. Compatible
to [D-BAS](https://gitlab.cs.uni-duesseldorf.de/project/dbas).

## Setup

Pull all bower components with:

    bower install

To get an interactive development environment run:

    rlwrap lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
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

#### devcards

Interactive tests! [devcards](https://github.com/bhauman/devcards) can be used
for visual tests with different states. Start them by using `leiningen`:

    lein figwheel devcards

Tests are defined as cards in `src/devcards/discuss/devcards/core.cljs`. You
*must* access this url if you run figwheel with the
devcards-parameter:
[http://localhost:3449/devcards/index.html](http://localhost:3449/devcards/index.html).

## License

Copyright © 2016-2017 Christian Meter

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
