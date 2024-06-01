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
      user <- runTransaction(
        UserDaoImpl.addUser(User.fromUserSource(userSource))
      )
      _ <- authService.createAuth(
        userLogin = userSource.login,
        password = userSource.password
      )
    } yield user
  }

  def getUser(userLogin: String): Task[Option[User]] = runTransaction(
    UserDaoImpl.getUser(userLogin)
  )

  def deleteUser(userLogin: String): Task[Unit] =
    for {
      _ <- authService.deleteAuth(userLogin)
      _ <- runTransaction(
        UserDaoImpl.deleteUser(userLogin)
      )
    } yield ()

}

object UserService {
  def live: ZLayer[Transactor[Task] with AuthService, Throwable, UserService] =
    ZLayer.fromFunction(new UserService(_, _))
}
