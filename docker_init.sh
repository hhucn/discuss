#!/usr/bin/env bash

# Get bower components
GIT_DIR=/tmp bower install --allow-root > /dev/null 2>&1

# Build minified js
lein cljsbuild once min

# Create CSS files
cd resources/public/
sass css/discuss.sass css/discuss.css --style compressed > /dev/null 2>&1
sass css/zeit.sass css/zeit.css --style compressed > /dev/null 2>&1
rm -rf .sass-cache

port=8888
python2 -m SimpleHTTPServer ${port}