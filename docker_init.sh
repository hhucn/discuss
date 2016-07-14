#!/usr/bin/env bash

# Build minified js
lein cljsbuild once min

# Create CSS files
cd resources/public/
sass css/discuss.sass css/discuss.css --style compressed
sass css/zeit.sass css/zeit.css --style compressed

# Print IP address
ip=`ip addr show eth0 | grep "inet\b" | awk '{print $2}' | cut -d/ -f1`
port=8000

printf "\n###################################################"
printf "\n# Connect to discuss via http://$ip:$port"
printf "\n###################################################\n"

python2 -m SimpleHTTPServer ${port}