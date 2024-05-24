package com.calendar

import io.grpc.StatusException
import io.grpc.protobuf.services.ProtoReflectionService
import scalapb.zio_grpc.Server
import scalapb.zio_grpc.ServerMain
import scalapb.zio_grpc.ServiceList
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio._
import zio.Console._
import calendar.calendar.ZioCalendar.Calendar
import com.calendar.api.CalendarImpl
import com.calendar.service.{AuthService, NoteService, UserService}
import com.calendar.storage.NoteDao
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.util.transactor.Transactor
import io.github.gaelrenoux.tranzactio.ConnectionSource
import io.github.gaelrenoux.tranzactio.doobie.Database
import scalapb.zio_grpc.ServerLayer
import zio.interop.catz.asyncInstance

object Main extends zio.ZIOAppDefault {
  val serverLayer = ServerLayer.fromServiceList(
    io.grpc.ServerBuilder
      .forPort(9090)
      .addService(ProtoReflectionService.newInstance()),
    ServiceList
      .addFromEnvironment[Calendar]
  )

  val dbConfig =
    ConfigSource.default
      .load[DbConfig]
      .getOrElse(throw new IllegalStateException("Config is wrong"))

  val xa: Transactor[Task] = Transactor.fromDriverManager[Task](
    "org.postgresql.Driver",
    dbConfig.postgresUri,
    dbConfig.postgresUsername,
    dbConfig.postgresPassword
  )

  val calendarProject = ZLayer.make[Server](
    ZLayer.succeed(xa),
    CalendarImpl.live,
    UserService.live,
    NoteService.live,
    AuthService.live,
    serverLayer
  )

  def run =
    (calendarProject.build *> ZIO.never).exitCode
}
