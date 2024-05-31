package com.calendar
import zio.{UIO, ULayer, ZIO, ZLayer}
import zio.macros.accessible
import java.util.{UUID => jUUID}

object UUID {
  def generateUUID: UIO[String] =
    ZIO.succeed(jUUID.randomUUID()).map(_.toString)
}
