package com.calendar.service
import cats.implicits.catsSyntaxApply
import com.calendar.UUID
import com.calendar.model.{Auth, User, UserSource}
import com.calendar.storage.impl.UserDaoImpl
import doobie.util.transactor.Transactor
import zio.{Task, ZLayer}

class UserService(transactor: Transactor[Task], authService: AuthService) {
  implicit val tr: Transactor[Task] = transactor

  def createUser(userSource: UserSource): Task[User] = {
    for {
      _ <- authService.createAuth(
        userLogin = userSource.login,
        password = userSource.password
      )
      user <- runTransaction(
        UserDaoImpl.addUser(User.fromUserSource(userSource))
      )
    } yield user
  }

  def getUser(userLogin: String): Task[Option[User]] = runTransaction(
    UserDaoImpl.getUser(userLogin)
  )

  def deleteUser(userLogin: String): Task[Unit] = runTransaction(
    UserDaoImpl.deleteUser(userLogin)
  )

}

object UserService {
  def live: ZLayer[Transactor[Task] with AuthService, Throwable, UserService] =
    ZLayer.fromFunction(new UserService(_, _))
}
