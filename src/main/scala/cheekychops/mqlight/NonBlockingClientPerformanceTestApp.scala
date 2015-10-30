/*
 * Copyright 2003-2015 Monitise Group Limited. All Rights Reserved.
 *
 * Save to the extent permitted by law, you may not use, copy, modify,
 * distribute or create derivative works of this material or any part
 * of it without the prior written consent of Monitise Group Limited.
 * Any reproduction of this material must contain this notice.
 */
package cheekychops.mqlight

import com.ibm.mqlight.api._
import collection.mutable.Set
import java.util.concurrent._
import scala.collection.JavaConverters._

object NonBlockingClientPerformanceTestApp extends App {

  val senderCount = 4
  val messageCount = 100

  val senders = new ConcurrentLinkedQueue[NonBlockingClient]()
  val topic = "/temp/topic"
  val subscriberTTL = 0
  val subscribeOptions = SubscribeOptions.builder.setTtl(subscriberTTL).build
  val timeoutMins = 3
  val sendOptions = SendOptions.builder().setTtl(timeoutMins * 60000L).build

  println(s"using $senderCount clients to send $messageCount messages to $topic")
  println(s"Message TTL = $timeoutMins minutes")
  println(s"Subscriber TTL = $subscriberTTL ms")

  //Create the senders
  createClients(senderCount) foreach { client =>
    senders.add(client)
  }

  //Perform the test with varying numbers of clients
  1 to 10 by 1 foreach { i =>
    performTest(i)
  }

  //Stop the senders
  stop(senders.asScala)

  def performTest(clientCount: Int) = {
    val started = System.currentTimeMillis
    val receivers = createClients(clientCount)
    val created = System.currentTimeMillis
    val destinationLatch = new CountDownLatch(messageCount * clientCount)
    subscribe(receivers, destinationLatch)
    val subscribed = System.currentTimeMillis
    sendMessages()
    destinationLatch.await(timeoutMins, TimeUnit.MINUTES)
    if (destinationLatch.getCount > 0) println(s"TIMEOUT: Still had ${destinationLatch.getCount} messages to receive")
    val received = System.currentTimeMillis
    unsubscribe(receivers)
    val unsubscribed = System.currentTimeMillis
    stop(receivers)
    val stopped = System.currentTimeMillis
    val receivedCount = messageCount * clientCount - destinationLatch.getCount
    val msPerMessage = (received - subscribed) / (receivedCount / clientCount)
    println(s"$clientCount clients took ${created - started} ms to create, ${subscribed - created} ms to subscribe, ${received - subscribed} ms to receive $receivedCount messages ($msPerMessage ms/msg sent), ${unsubscribed - received} ms to unsubscribe, and ${stopped - unsubscribed} ms to stop")
  }

  def createClients(count: Int): Iterable[NonBlockingClient] = {
    val clients = Set.empty[NonBlockingClient]
    val latch = new CountDownLatch(count)
    1 to count foreach { _ =>
      clients += NonBlockingClient.create(BrokerSettings.Uri.orNull, BrokerSettings.Options, new ClientListener(latch), null)
    }
    latch.await(2, TimeUnit.MINUTES)
    if (latch.getCount > 0) println(s"TIMEOUT: Still had ${latch.getCount} clients to create")
    clients
  }

  def subscribe(clients: Iterable[NonBlockingClient], destinationLatch: CountDownLatch) = {
    val subscriptionLatch = new CountDownLatch(clients.size)
    val destinationListener = new MyDestinationListener(destinationLatch)
    val subscriptionListener = new MyCompletionListener(subscriptionLatch)
    clients foreach { s =>
      s.subscribe(topic, subscribeOptions, destinationListener, subscriptionListener, null)
    }
    subscriptionLatch.await(2, TimeUnit.MINUTES)
    if (subscriptionLatch.getCount > 0) println(s"TIMEOUT: Still had ${subscriptionLatch.getCount} clients to subscribe")
  }

  def unsubscribe(subscribers: Iterable[NonBlockingClient]) = {
    val latch = new CountDownLatch(subscribers.size)
    val unsubscribeListener = new MyCompletionListener(latch)

    subscribers foreach { s =>
      s.unsubscribe(topic, unsubscribeListener, null)
    }
    latch.await(2, TimeUnit.MINUTES)
    if (latch.getCount > 0) println(s"TIMEOUT: Still had ${latch.getCount} clients to unsubscribe")
  }

  def stop(subscribers: Iterable[NonBlockingClient]) = {
    val latch = new CountDownLatch(subscribers.size)
    val stopListener = new MyCompletionListener(latch)

    subscribers foreach { s =>
      s.stop(stopListener, null)
    }
    latch.await(2, TimeUnit.MINUTES)
    if (latch.getCount > 0) println(s"TIMEOUT: Still had ${latch.getCount} clients to stop")
  }

  def sendMessages() = {
    val executor = Executors.newFixedThreadPool(senderCount)
    val task = new Runnable() {
      def run() = {
        val sender = senders.remove()
        sender.send(topic, "A Message", null, sendOptions, null, null)
        senders.offer(sender)
      }
    }

    1 to messageCount foreach { _ => executor.submit(task) }
    executor.shutdown()
    executor.awaitTermination(2, TimeUnit.MINUTES)
  }

  class ClientListener(latch: CountDownLatch) extends NonBlockingClientListener[Object] {
    override def onStarted(client: NonBlockingClient, context: Object) = latch.countDown()
    override def onStopped(client: NonBlockingClient, context: Object, exception: ClientException) = if (Option(exception).isDefined) exception.printStackTrace()
    override def onRestarted(client: NonBlockingClient, context: Object) = println("Restarted")
    override def onRetrying(client: NonBlockingClient, context: Object, exception: ClientException) = println(s"Retrying: $exception")
    override def onDrain(client: NonBlockingClient, context: Object) = {}
  }

  class MyDestinationListener(latch: CountDownLatch) extends DestinationListener[Object] {
    override def onMessage(client: NonBlockingClient, context: Object, delivery: Delivery) = latch.countDown()
    override def onMalformed(client: NonBlockingClient, context: Object, malformedDelivery: MalformedDelivery) = println(s"Malformed: $malformedDelivery")
    override def onUnsubscribed(client: NonBlockingClient, context: Object, topicPattern: String, share: String, error: Exception) = if (Option(error).isDefined) error.printStackTrace()
  }

  class MyCompletionListener(latch: CountDownLatch) extends CompletionListener[Object] {
    override def onSuccess(client: NonBlockingClient, context: Object) = latch.countDown()
    override def onError(client: NonBlockingClient, context: Object, error: java.lang.Exception) = error.printStackTrace()
  }

}
