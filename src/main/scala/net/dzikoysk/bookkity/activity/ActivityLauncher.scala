package net.dzikoysk.bookkity.activity

import java.util.concurrent.CompletableFuture

import ackcord.{APIMessage, ClientSettings, DiscordClient}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ActivityLauncher {

  val ActivityLogger: Logger = Logger(LoggerFactory.getLogger("Activity"))
  val Repository: ActivityRepository = new ActivityRepository()
  val Client = new CompletableFuture[DiscordClient]()

  def main(args: Array[String]): Unit = {
    val clientSettings = ClientSettings(args(0))
    val client = Await.result(clientSettings.createClient(), Duration.Inf)
    Client.complete(client)

    client.onEventSideEffectsIgnore {
      case APIMessage.Ready(_) =>
        ActivityLogger.info("Bookkity :: Activity is ready")
      case APIMessage.MessageCreate(_, message, _) =>
        ActivityLogger.info("Count: " + Repository.increment(message.authorId.toString))
    }

    val commands = new ActivityCommands(client.requests)
    client.commands.runNewNamedCommand(commands.ping)
    client.commands.runNewNamedCommand(commands.shutdown)

    Repository.load()
    client.login()

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = Repository.close()
    })
  }

  def shutdown(): Unit = {
    Client.thenAccept(client => {
      ActivityLogger.info("Shutting down service...")
      client.logout()

      Repository.close()
      client.shutdownJVM()
    })
  }

}