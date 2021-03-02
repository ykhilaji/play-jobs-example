#!/usr/bin/env bash

set -x

sbt -mem 2048 "~run -Dhttp.port=9002 -Dakka.remote.artery.canonical.port=2553  -Dkamon.prometheus.embedded-server.port=9097 -Dakka.management.http.port=8560"
#sbt -mem 2048 "~run -Dhttp.port=9001 -Dakka.remote.netty.tcp.port=2552"