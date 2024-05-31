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
    val userLogin = auth.userLogin
    val hashedPassword = auth.hashedPassword
    sql"insert into auth(user_login, hashed_password) values ($userLogin, $hashedPassword)".update.run
      .map(_ => auth)
  }

  override def getAuth(
      auth: Auth
  ): ConnectionIO[
    Option[Auth]
  ] = {
    val userLogin = auth.userLogin
    val hashedPassword = auth.hashedPassword
    sql"select user_login, hashed_password from auth where user_login = $userLogin and hashed_password = $hashedPassword"
      .query[Auth]
      .option
  }

  override def deleteAuth(
      userLogin: String
  ): ConnectionIO[Unit] =
    sql"delete from auth where user_login = $userLogin".update.run.map(_ => ())
}
