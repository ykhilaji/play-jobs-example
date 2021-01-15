#!/usr/bin/env bash

set -x

sbt -mem 2048 "~run -Dhttp.port=9000 -Dakka.remote.artery.canonical.port=2551  -Dkamon.prometheus.embedded-server.port=9095 -Dakka.management.http.port=8558 "
