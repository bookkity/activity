package net.dzikoysk.bookkity.activity

import ackcord.{APIMessage, ClientSettings}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ActivityLauncher {

  def main(args: Array[String]): Unit = {
    val clientSettings = ClientSettings(args(0))
    //The client settings contains an excecution context that you can use before you have access to the client
    //import clientSettings.executionContext

    //In real code, please dont block on the client construction
    val client = Await.result(clientSettings.createClient(), Duration.Inf)

    //The client also contains an execution context
    //import client.executionContext

    client.onEventSideEffectsIgnore {
      case APIMessage.Ready(_) => println("Now ready")
    }

    client.login()
  }

}