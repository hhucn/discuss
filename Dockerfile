FROM clojure:alpine
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

RUN apk update && apk add --no-cache fontconfig curl && \
  mkdir -p /usr/share && \
  cd /usr/share && \
  curl -L https://github.com/Overbryd/docker-phantomjs-alpine/releases/download/2.11/phantomjs-alpine-x86_64.tar.bz2 | tar xj && \
  ln -s /usr/share/phantomjs/phantomjs /usr/bin/phantomjs

RUN apk add --no-cache ruby nodejs python && \
    (gem install sass; exit 0) && \
    npm install bower -g && \
    mkdir /discuss

WORKDIR /discuss

ADD . /discuss

RUN lein deps > /dev/null 2>&1