FROM clojure:alpine
MAINTAINER Christian Meter <meter@cs.uni-duesseldorf.de>

RUN apk --update add nodejs ruby git python && \
    gem install sass --no-rdoc --no-ri && \
    npm install bower -g && \
    mkdir ./discuss

WORKDIR /discuss
ADD . /discuss

RUN bower install --allow-root && \
    lein do clean, cljsbuild once min

WORKDIR /discuss/resources/public/

RUN sass css/discuss.sass css/discuss.css --style compressed && \
    sass css/zeit.sass css/zeit.css --style compressed && \
    rm -rf .sass-cache

EXPOSE 8888
CMD ["python2", "-m", "SimpleHTTPServer", "8888"]
