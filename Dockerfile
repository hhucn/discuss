FROM clojure:alpine
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

RUN apk add --no-cache ruby nodejs python && \
    (gem install sass; exit 0) && \
    npm install bower -g && \
    mkdir /discuss

WORKDIR /discuss

ADD . /discuss

RUN lein deps > /dev/null 2>&1