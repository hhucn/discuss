FROM clojure:alpine
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

RUN apk --no-cache add yarn git && \
    yarn global add bower node-sass && \
    mkdir ./discuss

WORKDIR /discuss
COPY . /discuss

RUN GIT_DIR=/tmp bower install --allow-root

RUN node-sass resources/public/css/discuss.sass resources/public/css/discuss.css --style compressed && \
    node-sass resources/public/css/zeit.sass resources/public/css/zeit.css --style compressed && \
    rm -rf .sass-cache

WORKDIR /discuss

EXPOSE 3449
CMD ["lein", "figwheel"]
