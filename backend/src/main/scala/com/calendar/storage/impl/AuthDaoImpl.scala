package com.calendar.storage.impl
import com.calendar.model.Auth
import com.calendar.storage.AuthDao
import doobie.free.connection.ConnectionIO
import doobie.implicits.toSqlInterpolator

object AuthDaoImpl extends AuthDao {
  override def addAuth(
      auth: Auth
  ): ConnectionIO[
    Auth
  ] = {
    val id = auth.id
    val hashedPassword = auth.hashedPassword
    sql"insert into auth(id, hashed_password) values ($id, $hashedPassword)".update.run
      .map(_ => auth)
  }

  override def getAuth(
      auth: Auth
  ): ConnectionIO[
    Auth
  ] = {
    val id = auth.id
    val hashedPassword = auth.hashedPassword
    sql"select id, hashed_password from auth where id = $id and hashed_password = $hashedPassword"
      .query[Auth]
      .unique
  }

  override def deleteAuth(
      id: String
  ): ConnectionIO[Unit] =
    sql"delete from auth where id = $id".update.run.map(_ => ())
}
