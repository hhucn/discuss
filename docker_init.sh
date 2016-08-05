#!/usr/bin/env bash

# Get bower components
GIT_DIR=/tmp bower install --allow-root > /dev/null 2>&1

# Build minified js
lein deps > /dev/null 2>&1
lein cljsbuild once min

# Create CSS files
cd resources/public/
sass css/discuss.sass css/discuss.css --style compressed > /dev/null 2>&1
sass css/zeit.sass css/zeit.css --style compressed > /dev/null 2>&1
rm -rf .sass-cache

# Print IP address
ip=`ip addr show eth0 | grep "inet\b" | awk '{print $2}' | cut -d/ -f1`
port=8888

printf "\n###################################################"
printf "\n# Connect to discuss via http://$ip:$port"
printf "\n###################################################\n"

python2 -m SimpleHTTPServer ${port}