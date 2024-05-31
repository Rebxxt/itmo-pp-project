package com.calendar.storage

import com.calendar.model.{User}
import doobie.implicits._
import doobie.free.connection.ConnectionIO

trait UserDao {
  def addUser(user: User): ConnectionIO[User]
  def getUser(userLogin: String): ConnectionIO[Option[User]]
  def deleteUser(userLogin: String): ConnectionIO[Unit]

}
