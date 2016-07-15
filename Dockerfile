FROM clojure
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

RUN apt-get update
RUN apt-get install -yqq rubygems
RUN yes | gem install sass bower

RUN mkdir ./discuss
WORKDIR /discuss

ADD . /discuss

RUN lein deps

RUN bower install