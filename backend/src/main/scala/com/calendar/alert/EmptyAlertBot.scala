package com.calendar.alert
import zio.{Task, ZIO}

class EmptyAlertBot extends AlertBot {
  override def alert(message: String): Task[Unit] = ZIO.succeed(())
}
