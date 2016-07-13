FROM clojure
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

# figwheel WebSocket port
EXPOSE 3449

RUN mkdir ./discuss
WORKDIR /discuss

ADD . /discuss

RUN lein deps
# RUN lein figwheel devcards