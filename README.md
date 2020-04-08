## play-jobs

# Launch servers

For this first test kamon is installed and running on port 5266 and prometheus reposter is running on port  9095.
we cannot run multiple instance in the same machine

so we launch only one instance in the cluster for test

 $ sbt -Dhttp.port=9000 -Dakka.remote.netty.tcp.port=2551 run


## Setup client in scala

```

// Ideally run multiple clients with different users
var wb = new WebSocket("ws://localhost/<user>/ws") // where user is current user

// See Network tab

```

## Setup client in chrome-extension

for simple test use websocket client as chrome-extension  and connect it to the URL :
 ```ws://localhost:9000/<user>/ws```
 
 Launch the command below to test jobs


$ ab -m POST -k -c 2 -n 20 http://localhost:9000/`<user>`/jobs/10


## setup prometheus

Download prometheus from the URL https://prometheus.io/download/
Update prometheus.yml file with conf/prometheus.yml
Launch Prometheus


## Run stress test

Exemple : ab -m POST -k -c 2 -n 20 http://localhost:9000/elhaloui/jobs/10

###### -c <n> where n is number of concurrent requests, e. g: 2
###### -n <n> where n is total number of requests, e.g: 20
###### http://localhost/<user>/jobs/<n> where n is number of colis imported and `user` is the current user, e,g: user=elhaloui n=10


Project source : https://github.com/ykhilaji/play-jobs-example

