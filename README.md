# mqlight-testing #

Execute the following command to execute the NonBlockingClientPerformanceTestApp:

```
mvn scala:run -q

```

You should see output like this:

```

1 clients took 42 ms to create, 42 ms to subscribe, 5265 ms to receive 800 messages, 29 ms to unsubscribe, and 6 ms to stop
2 clients took 73 ms to create, 31 ms to subscribe, 4701 ms to receive 1600 messages, 25 ms to unsubscribe, and 1 ms to stop
3 clients took 67 ms to create, 26 ms to subscribe, 8908 ms to receive 2400 messages, 31 ms to unsubscribe, and 1 ms to stop
4 clients took 60 ms to create, 27 ms to subscribe, 8273 ms to receive 3200 messages, 52 ms to unsubscribe, and 2 ms to stop
5 clients took 75 ms to create, 32 ms to subscribe, 9974 ms to receive 4000 messages, 38 ms to unsubscribe, and 2 ms to stop
...

```
