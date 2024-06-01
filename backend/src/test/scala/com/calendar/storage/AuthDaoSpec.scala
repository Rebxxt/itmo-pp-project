package com.calendar.storage
import com.calendar.model.{Auth, User}
import com.calendar.service.runTransaction
import com.calendar.storage.impl.{AuthDaoImpl, UserDaoImpl}
import doobie.util.transactor.Transactor
import zio.test.Assertion.{anything, fails}
import zio.{Scope, Task, ZIO, ZLayer}
import zio.test._
import zio.test.Assertion._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

import scala.com.calendar.storage.UserDaoSpec.{test, testUser}
import scala.com.calendar.storage.prepareContainer

object AuthDaoSpec extends ZIOSpecDefault {
  private val testUser = User("Anna")
  private val testAuth = Auth(testUser.login, "password")
  override def spec: Spec[
    TestEnvironment with Scope,
    Any
  ] = {
    test("AuthDao.add should add auth") {
      (for {
        authDao <- ZIO.service[AuthDao]
        userDao <- ZIO.service[UserDao]
        transactor <- ZIO.service[Transactor[Task]]
        _ <- runTransaction(userDao.addUser(testUser))(transactor)
        addedAuth <- runTransaction(authDao.addAuth(testAuth))(transactor)
        gotAuth <- runTransaction(authDao.getAuth(testAuth))(transactor)
        nonExistingAuth <- runTransaction(
          authDao.getAuth(
            testAuth.copy(userLogin = testAuth.userLogin + "fake")
          )
        )(transactor)
      } yield assertTrue(
        addedAuth == testAuth && gotAuth.contains(
          testAuth
        ) && nonExistingAuth.isEmpty
      ))
        .provide(
          ZLayer.succeed(AuthDaoImpl),
          ZLayer.succeed(UserDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    } + test("AuthDao.delete should delete auth") {
      (for {
        authDao <- ZIO.service[AuthDao]
        userDao <- ZIO.service[UserDao]
        transactor <- ZIO.service[Transactor[Task]]
        _ <- runTransaction(userDao.addUser(testUser))(transactor)
        _ <- runTransaction(authDao.addAuth(testAuth))(transactor)
        _ <- runTransaction(authDao.deleteAuth(testAuth.userLogin))(transactor)
        nonExistingAuth <- runTransaction(authDao.getAuth(testAuth))(
          transactor
        )
      } yield assertTrue(nonExistingAuth.isEmpty))
        .provide(
          ZLayer.succeed(AuthDaoImpl),
          ZLayer.succeed(UserDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    } + test("AuthDao.add should fail if user not in users") {
      (for {
        authDao <- ZIO.service[AuthDao]
        transactor <- ZIO.service[Transactor[Task]]
        result <- assertZIO(
          runTransaction(authDao.addAuth(testAuth))(transactor).exit
        )(fails(anything))
      } yield result)
        .provide(
          ZLayer.succeed(AuthDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    }
  }
}
