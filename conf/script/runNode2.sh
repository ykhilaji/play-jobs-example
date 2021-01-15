#!/usr/bin/env bash

set -x

sbt -mem 2048 "~run -Dhttp.port=9001 -Dakka.remote.artery.canonical.port=2552  -Dkamon.prometheus.embedded-server.port=9096 -Dakka.management.http.port=8559"
#sbt -mem 2048 "~run -Dhttp.port=9001 -Dakka.remote.netty.tcp.port=2552"