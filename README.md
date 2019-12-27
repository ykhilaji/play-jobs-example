## play-jobs

# Launch servers

sbt -Dhttp.port=9000 -Dakka.remote.netty.tcp.port=2551
sbt -Dhttp.port=9001 -Dakka.remote.netty.tcp.port=2552
sbt -Dhttp.port=9002 -Dakka.remote.netty.tcp.port=2553
sbt -Dhttp.port=9003 -Dakka.remote.netty.tcp.port=2554

## Setup client

```

// Ideally run multiple clients with different users
var wb = new WebSocket("ws://localhost/<user>/ws") // where user is current user

// See Network tab

```

## Run stress test

ab -m POST -k -c 250 -n 2000 http://localhost/<user>/jobs/10000

# -c <n> where n is number of concurrent requests, e. g: 250
# -n <n> where n is total number of requests, e.g: 2000
# http://localhost/<user>/jobs/<n> where n is number of colis imported and `user` is the current user, e,g: user=yk n=10000

