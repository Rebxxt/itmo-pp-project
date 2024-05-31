package com.calendar.alert
import com.calendar.alert.impl.TelegramAlertBot
import com.calendar.config.TelegramConfig
import zio.{Task, ZIO, ZLayer}

trait AlertBot {
  def alert(message: String): Task[Unit]
}

object AlertBot {
  def live: ZLayer[TelegramConfig, Nothing, AlertBot] = {
    ZLayer {
      for {
        config <- ZIO.service[TelegramConfig]
      } yield {
        if (config.token.nonEmpty) {
          new TelegramAlertBot(config.token, config.chatId)
        } else {
          new EmptyAlertBot
        }
      }
    }
  }
}
