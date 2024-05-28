package com.calendar.alert.impl

import com.bot4s.telegram.cats.TelegramBot
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.models.ChatId
import com.calendar.alert.AlertBot
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio.Task
import zio.interop.catz._

class TelegramAlertBot(val token: String, chatId: String)
    extends TelegramBot[Task](
      token,
      AsyncHttpClientZioBackend
        .usingClient(zio.Runtime.default, asyncHttpClient())
    )
    with AlertBot {

  override def alert(message: String): Task[Unit] = {
    val messageRequest = SendMessage(ChatId(chatId), s"Alert: $message")
    request(messageRequest).unit
  }

}
