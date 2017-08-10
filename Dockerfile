FROM clojure:alpine
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

RUN echo -e 'http://dl-cdn.alpinelinux.org/alpine/edge/main\nhttp://dl-cdn.alpinelinux.org/alpine/edge/community\nhttp://dl-cdn.alpinelinux.org/alpine/edge/testing' > /etc/apk/repositories && \
    apk --no-cache add yarn ruby ruby-dev git python && \
    gem install sass --no-rdoc --no-ri && \
    yarn global add bower && \
    mkdir ./discuss

WORKDIR /discuss
COPY . /discuss

RUN GIT_DIR=/tmp bower install --allow-root && \
    lein do clean, cljsbuild once min

WORKDIR /discuss/resources/public/

RUN sass css/discuss.sass css/discuss.css --style compressed && \
    sass css/zeit.sass css/zeit.css --style compressed && \
    rm -rf .sass-cache

EXPOSE 80
CMD ["python2", "-m", "SimpleHTTPServer", "80"]
