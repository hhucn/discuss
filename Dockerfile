FROM clojure
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

RUN apt-get update
RUN apt-get install -yqq rubygems
RUN yes | gem install sass

RUN mkdir ./discuss
WORKDIR /discuss

ADD . /discuss

RUN lein deps
# RUN lein figwheel devcards