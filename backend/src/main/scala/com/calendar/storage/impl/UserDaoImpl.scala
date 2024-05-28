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
    val id = user.id
    val name = user.name
    sql"insert into users(id, name) values ($id, $name)".update.run
      .map(_ => user)
  }
  override def getUser(
      userId: String
  ): ConnectionIO[
    Option[User]
  ] = sql"select id, name from users where id = $userId".query[User].option

  override def deleteUser(
      userId: String
  ): ConnectionIO[Unit] =
    sql"delete from users where id = $userId".update.run.map(_ => ())
}
