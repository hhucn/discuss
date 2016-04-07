# discuss-cljs

Minimal front-end to include dialogue-based discussion systems into existing websites. Compatible to [D-BAS](https://gitlab.cs.uni-duesseldorf.de/project/dbas).

## Setup

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

### Testing

Tests can be found in `test/discuss/*_test.cljs`. These tests are automatically executed
when running an interactive environment but currently need to be compiled in a separate process:

    lein cljsbuild auto test

The tests require [phantomjs](http://phantomjs.org/) (`$ pacman -S phantomjs`).

## License

Copyright © 2016 Christian Meter

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
