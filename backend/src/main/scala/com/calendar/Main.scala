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
import com.calendar.alert.AlertBot
import com.calendar.api.CalendarImpl
import com.calendar.api.http.ApiHandler
import com.calendar.config.{DbConfig, TelegramConfig}
import com.calendar.service.{AuthService, NoteService, UserService}
import com.calendar.storage.NoteDao
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.util.transactor.Transactor
import io.github.gaelrenoux.tranzactio.ConnectionSource
import io.github.gaelrenoux.tranzactio.doobie.Database
import scalapb.zio_grpc.ServerLayer
import zio.interop.catz.asyncInstance

object Main extends zio.ZIOAppDefault {
//  val serverLayer = ServerLayer.fromServiceList(
//    io.grpc.ServerBuilder
//      .forPort(9090)
//      .addService(ProtoReflectionService.newInstance()),
//    ServiceList
//      .addFromEnvironment[Calendar]
//  )

  private val dbConfig =
    ConfigSource.default
      .at("postgres")
      .load[DbConfig]
      .getOrElse(throw new IllegalStateException("Config is wrong"))

  private val telegramConfig = ConfigSource.default
    .at("telegram")
    .load[TelegramConfig]
    .getOrElse(throw new IllegalStateException("Config is wrong"))

  val transactor: Transactor[Task] = Transactor.fromDriverManager[Task](
    "org.postgresql.Driver",
    dbConfig.postgresUri,
    dbConfig.postgresUsername,
    dbConfig.postgresPassword
  )

  private val calendarProject = ZLayer.make[ApiHandler](
    ZLayer.succeed(transactor),
    ZLayer.succeed(telegramConfig),
    CalendarImpl.live,
    UserService.live,
    NoteService.live,
    AuthService.live,
    AlertBot.live,
    ApiHandler.live
//    serverLayer
  )

  def run =
    (for {
      apiHandler <- ZIO.service[ApiHandler]
      _ <- apiHandler.run
    } yield ()).provide(calendarProject).exitCode
}
