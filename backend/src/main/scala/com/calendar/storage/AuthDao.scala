package com.calendar.storage

import com.calendar.model.{Auth, Note, NoteSource, User}
import zio._
import doobie.implicits._
import com.calendar.storage.impl.NoteDaoImpl
import doobie.free.connection.ConnectionIO

trait AuthDao {
  def addAuth(auth: Auth): ConnectionIO[Auth]
  def getAuth(auth: Auth): ConnectionIO[Option[Auth]]
  def deleteAuth(userLogin: String): ConnectionIO[Unit]
}
