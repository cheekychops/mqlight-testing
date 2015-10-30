# mqlight-testing #

Execute the following command to execute the NonBlockingClientPerformanceTestApp:

```
mvn scala:run -q

```

You should see output like this:

```

using 4 clients to send 100 messages to /temp/topic
Message TTL = 3 minutes
Subscriber TTL = 0 ms
1 clients took 54 ms to create, 34 ms to subscribe, 447 ms to receive 100 messages (4 ms/msg sent), 12 ms to unsubscribe, and 5 ms to stop
11 clients took 255 ms to create, 47 ms to subscribe, 2635 ms to receive 1100 messages (26 ms/msg sent), 21 ms to unsubscribe, and 12 ms to stop
21 clients took 467 ms to create, 76 ms to subscribe, 2213 ms to receive 2100 messages (22 ms/msg sent), 84 ms to unsubscribe, and 38 ms to stop
31 clients took 643 ms to create, 113 ms to subscribe, 3992 ms to receive 3100 messages (39 ms/msg sent), 51 ms to unsubscribe, and 24 ms to stop
41 clients took 401 ms to create, 76 ms to subscribe, 3701 ms to receive 4100 messages (37 ms/msg sent), 22 ms to unsubscribe, and 37 ms to stop
51 clients took 471 ms to create, 90 ms to subscribe, 8122 ms to receive 5100 messages (81 ms/msg sent), 26 ms to unsubscribe, and 32 ms to stop
61 clients took 419 ms to create, 50 ms to subscribe, 10194 ms to receive 6100 messages (101 ms/msg sent), 21 ms to unsubscribe, and 32 ms to stop
71 clients took 465 ms to create, 101 ms to subscribe, 11794 ms to receive 7100 messages (117 ms/msg sent), 24 ms to unsubscribe, and 25 ms to stop
81 clients took 543 ms to create, 96 ms to subscribe, 15178 ms to receive 8100 messages (151 ms/msg sent), 25 ms to unsubscribe, and 46 ms to stop
91 clients took 721 ms to create, 29 ms to subscribe, 16455 ms to receive 9100 messages (164 ms/msg sent), 24 ms to unsubscribe, and 45 ms to stop
101 clients took 786 ms to create, 28 ms to subscribe, 14624 ms to receive 10100 messages (146 ms/msg sent), 21 ms to unsubscribe, and 42 ms to stop
111 clients took 628 ms to create, 22 ms to subscribe, 20727 ms to receive 11100 messages (207 ms/msg sent), 33 ms to unsubscribe, and 32 ms to stop
121 clients took 679 ms to create, 23 ms to subscribe, 17370 ms to receive 12100 messages (173 ms/msg sent), 26 ms to unsubscribe, and 44 ms to stop
131 clients took 787 ms to create, 23 ms to subscribe, 15976 ms to receive 13100 messages (159 ms/msg sent), 47 ms to unsubscribe, and 53 ms to stop
141 clients took 861 ms to create, 22 ms to subscribe, 19745 ms to receive 14100 messages (197 ms/msg sent), 21 ms to unsubscribe, and 49 ms to stop
151 clients took 787 ms to create, 22 ms to subscribe, 12469 ms to receive 15100 messages (124 ms/msg sent), 24 ms to unsubscribe, and 48 ms to stop
161 clients took 845 ms to create, 24 ms to subscribe, 12572 ms to receive 16100 messages (125 ms/msg sent), 28 ms to unsubscribe, and 118 ms to stop
171 clients took 1527 ms to create, 99 ms to subscribe, 10021 ms to receive 17100 messages (100 ms/msg sent), 36 ms to unsubscribe, and 79 ms to stop
181 clients took 1238 ms to create, 28 ms to subscribe, 12933 ms to receive 18100 messages (129 ms/msg sent), 27 ms to unsubscribe, and 51 ms to stop
191 clients took 977 ms to create, 63 ms to subscribe, 12491 ms to receive 19100 messages (124 ms/msg sent), 18 ms to unsubscribe, and 61 ms to stop
201 clients took 1028 ms to create, 25 ms to subscribe, 12931 ms to receive 20100 messages (129 ms/msg sent), 30 ms to unsubscribe, and 65 ms to stop
...

```

To deploy to BlueMix:

```

cf push mqlight-testing -p target/mqlight-testing-0.0.1-SNAPSHOT-jar-with-dependencies.jar --no-route --no-start
cf cs mqlightdedicated dedicated mqlight-service
cf bs mqlight-testing mqlight-service
cf start mqlight-testing

```




