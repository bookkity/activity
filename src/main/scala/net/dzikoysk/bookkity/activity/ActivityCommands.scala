package net.dzikoysk.bookkity.activity

import java.time.{Duration, ZonedDateTime}

import ackcord.Requests
import ackcord.commands.{CommandBuilder, CommandController, GuildUserCommandMessage, NamedCommand}
import ackcord.data.Permission
import ackcord.syntax.TextChannelSyntax
import akka.NotUsed

class ActivityCommands(requests: Requests) extends CommandController(requests) {

  val ping: NamedCommand[NotUsed] =
    Command
      .named(Seq("!activity"), Seq("ping"))
      .withRequest(request => {
        val message = request.message
        val difference = Duration.between(message.timestamp, ZonedDateTime.now(message.timestamp.getOffset)).abs()
        request.textChannel.sendMessage(s"Ping: ${difference.toMillis}ms")
      })

  private val ElevatedCommand: CommandBuilder[GuildUserCommandMessage, NotUsed] =
    GuildCommand.andThen(CommandBuilder.needPermission[GuildUserCommandMessage](Permission.Administrator))

  val shutdown: NamedCommand[NotUsed] =
    ElevatedCommand
      .named(Seq("!activity"), Seq("shutdown"))
      .withRequest(request => {
        ActivityLauncher.shutdown()
        request.textChannel.sendMessage("Shutting down activity service...")
      })

}
