package com.calendar
import zio.{Scope, ZIO}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object UUIDSpec extends ZIOSpecDefault {

  private val nTimes = 100000
  override def spec: Spec[
    TestEnvironment with Scope,
    Any
  ] = {
    test("UUID.generateUUID") {
      (for {
        uuidSeq <- ZIO.collectAll(ZIO.replicate(nTimes)(UUID.generateUUID))
      } yield assertTrue(uuidSeq.toSet.size == nTimes))
    }
  }
}
