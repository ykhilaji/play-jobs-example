#!/usr/bin/env bash

set -x

sbt -mem 2048 "~run -Dhttp.port=9001 -Dpekko.remote.artery.canonical.port=7355"