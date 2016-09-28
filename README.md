# discuss-cljs

[![build status](https://gitlab.cs.uni-duesseldorf.de/project/discuss/badges/master/build.svg)](https://gitlab.cs.uni-duesseldorf.de/project/discuss/commits/master)

Minimal front-end to include dialog-based discussion systems into existing websites. Compatible to [D-BAS](https://gitlab.cs.uni-duesseldorf.de/project/dbas).

## Setup

Pull all bower components with:

    bower install

To get an interactive development environment run:

    lein figwheel

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

Interactive tests! I am now using [devcards](https://github.com/bhauman/devcards) for visual tests with
different states. Start them by using `leiningen`:

    lein figwheel devcards

Tests are defined as cards in `src/devcards/discuss/devcards/core.cljs`. You *must* access this
url if you run figwheel with the devcards-parameter:
[http://localhost:3449/devcards/index.html](http://localhost:3449/devcards/index.html).

This is currently the best profile for development.

## License

Copyright © 2016 Christian Meter

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
