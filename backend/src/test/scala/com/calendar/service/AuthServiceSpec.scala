package com.calendar.service
import zio.{Scope, ZIO, Task}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

import scala.util.Random

object AuthServiceSpec extends ZIOSpecDefault {
  private val nTimes = 100000
  private val stringSize = 15
  override def spec: Spec[
    TestEnvironment with Scope,
    Any
  ] = {
    test("AuthService.hashString") {
      val random = new Random()
      for {
        randomStrings <- ZIO.collectAll(
          ZIO.replicate(nTimes)(
            ZIO.attempt(random.alphanumeric.take(stringSize).mkString)
          )
        )
        hashedStrings = randomStrings.map(AuthService.hashString)
      } yield assertTrue(
        randomStrings.toSet.intersect(hashedStrings.toSet).isEmpty
      )
    }
  }
}
