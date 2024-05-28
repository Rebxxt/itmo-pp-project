package com.calendar.service
import com.calendar.model.Auth
import com.calendar.service.AuthService.hashString
import com.calendar.storage.impl.AuthDaoImpl
import doobie.util.transactor.Transactor
import zio.{Task, ZIO, ZLayer}

import java.security.MessageDigest

class AuthService(transactor: Transactor[Task]) {
  implicit val tr: Transactor[Task] = transactor

  def createAuth(id: String, password: String): Task[Auth] = {
    val hashedPassword =
      hashString(password)
    runTransaction(
      AuthDaoImpl.addAuth(Auth(id = id, hashedPassword = hashedPassword))
    )
  }

  def deleteAuth(id: String): Task[Unit] = {
    runTransaction(AuthDaoImpl.deleteAuth(id))
  }

  def authenticateUser(id: String, password: String): Task[Boolean] = {
    val hashedPassword =
      hashString(password)
    runTransaction(
      AuthDaoImpl.getAuth(Auth(id = id, hashedPassword = hashedPassword))
    ).map(_.nonEmpty)
  }

}

object AuthService {
  private def hashString(password: String): String = {
    val hashedPassword =
      MessageDigest.getInstance("MD5").digest(password.getBytes)
    hashedPassword.map("%02x".format(_)).mkString

  }

  def live: ZLayer[Transactor[Task], Throwable, AuthService] =
    ZLayer.fromFunction(new AuthService(_))
}
