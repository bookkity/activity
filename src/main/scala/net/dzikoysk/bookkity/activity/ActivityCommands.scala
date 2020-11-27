package net.dzikoysk.bookkity.activity

import java.time.{Duration, ZonedDateTime}

import ackcord.Requests
import ackcord.commands.{CommandController, NamedCommand}
import ackcord.syntax.TextChannelSyntax
import akka.NotUsed

class ActivityCommands(requests: Requests) extends CommandController(requests) {

  val ping: NamedCommand[NotUsed] = Command
    .named(Seq("!activity"), Seq("ping"))
    .withRequest(request => {
      val message = request.message
      val difference = Duration.between(message.timestamp, ZonedDateTime.now(message.timestamp.getOffset)).abs()
      request.textChannel.sendMessage(s"Ping: ${difference.toMillis}ms")
    })

}
