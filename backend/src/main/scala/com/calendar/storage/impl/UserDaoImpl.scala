package com.calendar.storage.impl
import com.calendar.model.{Note, User}
import com.calendar.storage.UserDao
import doobie.implicits.toSqlInterpolator
import doobie.free.connection.ConnectionIO

object UserDaoImpl extends UserDao {
  override def addUser(
      user: User
  ): ConnectionIO[
    User
  ] = {
    val login = user.login
    sql"insert into users(login) values ($login)".update.run
      .map(_ => user)
  }
  override def getUser(
      userLogin: String
  ): ConnectionIO[
    Option[User]
  ] = sql"select login from users where login = $userLogin".query[User].option

  override def deleteUser(
      userLogin: String
  ): ConnectionIO[Unit] =
    sql"delete from users where login = $userLogin".update.run.map(_ => ())
}
