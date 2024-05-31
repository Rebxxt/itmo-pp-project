package scala.com.calendar.storage
import com.calendar.model.User
import com.calendar.service.runTransaction
import com.calendar.storage.UserDao
import com.calendar.storage.impl.UserDaoImpl
import doobie.util.transactor.Transactor
import zio.test.Assertion.{anything, fails, isFailure}
import zio.{Scope, Task, ZIO, ZLayer}
import zio.test._
import zio.test.Assertion._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, assertZIO}

object UserDaoSpec extends ZIOSpecDefault {
  private val testUser = User("Anna")
  override def spec: Spec[
    TestEnvironment with Scope,
    Any
  ] = {
    test("UserDao.add") {
      (for {
        userDao <- ZIO.service[UserDao]
        transactor <- ZIO.service[Transactor[Task]]
        addedUser <- runTransaction(userDao.addUser(testUser))(transactor)
        gotUser <- runTransaction(userDao.getUser(testUser.login))(transactor)
        nonExistingUser <- runTransaction(
          userDao.getUser(testUser.login + "fake")
        )(transactor)
      } yield assertTrue(
        addedUser == testUser && gotUser.contains(
          testUser
        ) && nonExistingUser.isEmpty
      ))
        .provide(
          ZLayer.succeed(UserDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    } + test("UserDao.delete") {
      (for {
        userDao <- ZIO.service[UserDao]
        transactor <- ZIO.service[Transactor[Task]]
        _ <- runTransaction(userDao.addUser(testUser))(transactor)
        _ <- runTransaction(userDao.deleteUser(testUser.login))(transactor)
        nonExistingUser <- runTransaction(userDao.getUser(testUser.login))(
          transactor
        )
      } yield assertTrue(nonExistingUser.isEmpty))
        .provide(
          ZLayer.succeed(UserDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    }
  }
}
