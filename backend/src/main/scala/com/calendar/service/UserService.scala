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
      id <- UUID.generateUUID
      _ <- authService.createAuth(id = id, password = userSource.password)
      user <- runTransaction(
        UserDaoImpl.addUser(User.fromUserSource(userSource, id))
      )
    } yield user
  }

  def getUser(userId: String): Task[User] = runTransaction(
    UserDaoImpl.getUser(userId)
  )

  def deleteUser(userId: String): Task[Unit] = runTransaction(
    UserDaoImpl.deleteUser(userId)
  )

}

object UserService {
  def live: ZLayer[Transactor[Task] with AuthService, Throwable, UserService] =
    ZLayer.fromFunction(new UserService(_, _))
}
