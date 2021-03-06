# discuss

| Develop | Master | Release | Documentation |
|---------|--------|---------|---------------|
|[![CircleCI](https://img.shields.io/circleci/project/hhucn/discuss/develop.svg?maxAge=60)](https://circleci.com/gh/hhucn/discuss/tree/develop) | [![CircleCI](https://img.shields.io/circleci/project/hhucn/discuss/master.svg?maxAge=60)](https://circleci.com/gh/hhucn/discuss/tree/master) | [![GitHub release](https://img.shields.io/github/release/hhucn/discuss.svg?maxAge=60)](https://github.com/hhucn/discuss/releases) | [![Docs](https://img.shields.io/badge/docs-develop-blue)](https://cn-tsn.pages.cs.uni-duesseldorf.de/project/dbas/discuss/)

Minimal front-end to include dialog-based discussion systems into existing
websites. Compatible to [D-BAS](https://github.com/hhucn/dbas).


## Setup

The following tools are required:
* [Leiningen](http://leiningen.org/)
* [yarn](https://yarnpkg.com/)
* [sassc](https://github.com/sass/sassc)
* JDK12
* make

Build the project with:

    make build

To get an interactive development environment run:

    make run

and open your browser at [localhost:9500](http://localhost:9500). This will auto
compile and send all changes to the browser without the need to reload. After
the compilation process is complete, you will get a Browser Connected REPL. An
easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    make clean

To create a production build run:

    make min

And open your browser in `resources/public/index.html`. You will not get live
reloading, nor a REPL.

### Dependencies

For caching optimizations, the project stores the dependencies in a local `.m2`
folder. To avoid having duplicate dependencies on the development machine,
you can create a symbolic link from your user's .m2 folder to the local one by
executing this command: 

    ln -s ~/.m2 .m2


### Testing and Development

The testing environment is automatically launched when you start the
dev-environment of this project. You can then access the test-page at
http://localhost:9500/tests.html

The components are development with the help of
[devcards](https://github.com/bhauman/devcards). Access the project's devcards
at http://localhost:9500/cards.html


## Documentation

The latest documentation can be found at this location:
https://cn-tsn.pages.cs.uni-duesseldorf.de/project/dbas/discuss/


## Publications

This tool is being used in research projects (see [Google
Scholar](https://scholar.google.de/scholar?cites=16210704751689306722&as_sdt=2005&sciodt=0,5&hl=de)).
Here are several references:

Alexander Schneider and Christian Meter. "Various Efforts of Enhancing Real
World Online Discussions", 3rd European Conference on Argumentation – ECA, 2019.

Tobias Krauthoff, Christian Meter, Michael Baurmann, Gregor Betz and Martin
Mauve. "D-BAS -- A Dialog-Based Online Argumentation System", Computational
Models of Argument, 2018.

Christian Meter, Alexander Schneider and Martin Mauve. "EDEN: Extensible
Discussion Entity Network", Computational Models of Argument, 2018.

Alexander Schneider and Christian Meter. "Reusable Statements in Dialog-Based
Argumentation Systems." AI^ 3@ AI* IA. 2017.

Christian Meter, Tobias Krauthoff and Martin Mauve. "discuss: Embedding
dialog-based Discussions into Websites." International Conference on Learning
and Collaboration Technologies. Springer, Cham, 2017.

Tobias Krauthoff and Christian Meter. "Dialogbasierte Argumentation und ihre
Einbettung im Web." 2017.

## License

Copyright © 2016 - today hhucn

Distributed under the [MIT License](LICENSE).
