FROM clojure
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

# Add sources for nodejs
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash -

RUN apt-get update -qq
RUN apt-get install -yqq rubygems nodejs
RUN yes | gem install sass
RUN npm install bower -g

RUN mkdir ./discuss
WORKDIR /discuss

ADD . /discuss

RUN lein deps > /dev/null 2>&1