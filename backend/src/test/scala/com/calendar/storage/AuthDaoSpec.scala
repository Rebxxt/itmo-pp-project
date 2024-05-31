package com.calendar.storage
import com.calendar.model.Auth
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
  private val testAuth = Auth("id", "password")
  override def spec: Spec[
    TestEnvironment with Scope,
    Any
  ] = {
    test("AuthDao.add") {
      (for {
        authDao <- ZIO.service[AuthDao]
        transactor <- ZIO.service[Transactor[Task]]
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
          ZLayer.fromZIO(prepareContainer)
        )
    } + test("AuthDao.delete") {
      (for {
        authDao <- ZIO.service[AuthDao]
        transactor <- ZIO.service[Transactor[Task]]
        _ <- runTransaction(authDao.addAuth(testAuth))(transactor)
        _ <- runTransaction(authDao.deleteAuth(testAuth.userLogin))(transactor)
        nonExistingAuth <- runTransaction(authDao.getAuth(testAuth))(
          transactor
        )
      } yield assertTrue(nonExistingAuth.isEmpty))
        .provide(
          ZLayer.succeed(AuthDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    }
  }
}
