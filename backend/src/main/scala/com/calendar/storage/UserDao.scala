package com.calendar.storage

import com.calendar.model.{Note, NoteSource, User}
import zio._
import doobie.implicits._
import com.calendar.storage.impl.NoteDaoImpl
import doobie.free.connection.ConnectionIO

trait UserDao {
  def addUser(user: User): ConnectionIO[User]
  def getUser(userId: String): ConnectionIO[Option[User]]
  def deleteUser(noteId: String): ConnectionIO[Unit]

}
