package net.dzikoysk.bookkity.activity

import java.util.concurrent.{CompletableFuture, Executors, ScheduledFuture, TimeUnit}

import ackcord.{APIMessage, ClientSettings, DiscordClient}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import ackcord.data.TextGuildChannel
import ackcord.syntax.TextChannelSyntax

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object ActivityLauncher {

  val ActivityLogger: Logger = Logger(LoggerFactory.getLogger("Activity"))
  val Repository: ActivityRepository = new ActivityRepository()
  val Client: CompletableFuture[DiscordClient] = new CompletableFuture[DiscordClient]()

  val SleepMessage = "Pora się zajebać, zapierdolić"
  val SleepChannels: Array[String] = Array("pandasite", "secret")

  def main(args: Array[String]): Unit = {
    val clientSettings = ClientSettings(args(0))
    val client = Await.result(clientSettings.createClient(), Duration.Inf)
    Client.complete(client)

    val selectedSleepChannels = mutable.HashSet[TextGuildChannel]()
    val scheduler = Executors.newScheduledThreadPool(1)
    var sleepTask: Option[ScheduledFuture[_]] = None

    client.onEventSideEffects { implicit c => {
      case APIMessage.Ready(cache) =>
        ActivityLogger.info("Bookkity :: Activity is ready")
        ActivityLogger.info("Current time: " + LocalDateTime.now().toString)

        val todayMidnight = LocalDateTime.now.until(LocalDate.now.atStartOfDay.plusHours(4), ChronoUnit.MINUTES)
        val midnight = if (todayMidnight > 0) todayMidnight else LocalDateTime.now.until(LocalDate.now.atStartOfDay.plusHours(24 + 4), ChronoUnit.MINUTES)
        ActivityLogger.info("Time until midnight: " + midnight.toString + "min")

        sleepTask = sleepTask.orElse({
          ActivityLogger.info("Sleep task has been registered")

          Some(scheduler.scheduleAtFixedRate(() => {
            selectedSleepChannels.foreach(channel => {
              client.requestsHelper.run(channel.sendMessage(SleepMessage))
            })
          }, midnight, 1440, TimeUnit.MINUTES))
        })
      case APIMessage.GuildCreate(guild, createCache) =>
        guild.channels.values
          .filter(channel => channel.isInstanceOf[TextGuildChannel])
          .map(channel => channel.asInstanceOf[TextGuildChannel])
          .filter(channel => SleepChannels.contains(channel.name))
          .filter(channel => !selectedSleepChannels.exists(registered => registered.name == channel.name))
          .foreach(channel => {
            ActivityLogger.info("Sleep channel registered: " + channel.name)
            selectedSleepChannels.addOne(channel)
          })
      case APIMessage.MessageCreate(guild, message, cache) =>
        Repository.increment(message.authorId.toString)
    }}

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