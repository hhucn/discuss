FROM clojure
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

# Add sources for nodejs
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash -

RUN apt-get update -qq && \
    apt-get install -yqq rubygems nodejs && \
    yes | gem install sass && \
    npm install bower phantomjs-prebuilt -g && \
    mkdir ./discuss

WORKDIR /discuss

ADD . /discuss

RUN lein deps > /dev/null 2>&1