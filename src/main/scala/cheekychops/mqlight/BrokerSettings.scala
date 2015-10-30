/*
 * Copyright 2003-2015 Monitise Group Limited. All Rights Reserved.
 *
 * Save to the extent permitted by law, you may not use, copy, modify,
 * distribute or create derivative works of this material or any part
 * of it without the prior written consent of Monitise Group Limited.
 * Any reproduction of this material must contain this notice.
 */
package cheekychops.mqlight

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import com.ibm.mqlight.api.ClientOptions
import com.typesafe.config.{ Config, ConfigFactory }
import scala.collection.JavaConverters._

object BrokerSettings {
  private val Path = ConfigFactory.defaultReference().getConfig("broker")

  val Username = Path.getString("username")
  val Password = Path.getString("password")

  // when VCAP_SERVICES is defined the ibm async library will lookup the connection endpoints automatically
  private val (uri, options) = System.getenv("VCAP_SERVICES") match {
    case _: String =>
      val componentUri: Option[String] = None
      val usernameProperties = ClientOptions.builder().setCredentials(Username, Password).build()
      (componentUri, usernameProperties)
    case _ =>
      val host = Path.getString("host")
      val port = Path.getInt("port")
      val componentUri: Option[String] = Some(s"amqp://$Username:$Password@$host:$port")
      (componentUri, ClientOptions.builder().build())
  }

  val Uri = uri
  val Options = options
}
