package com.calendar

import io.grpc.StatusException
import io.grpc.protobuf.services.ProtoReflectionService
import scalapb.zio_grpc.Server
import scalapb.zio_grpc.ServerMain
import scalapb.zio_grpc.ServiceList
import zio._
import zio.Console._
import calendar.calendar.ZioCalendar.Calendar
import com.calendar.api.CalendarImpl
import com.calendar.service.NoteService
import com.calendar.storage.NoteDao
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.github.gaelrenoux.tranzactio.ConnectionSource
import io.github.gaelrenoux.tranzactio.doobie.Database
import scalapb.zio_grpc.ServerLayer

object Main extends zio.ZIOAppDefault {
  val serverLayer = ServerLayer.fromServiceList(
    io.grpc.ServerBuilder
      .forPort(9090)
      .addService(ProtoReflectionService.newInstance()),
    ServiceList
      .addFromEnvironment[Calendar]
  )

  val datasource = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/calendar")
    hikariConfig.setUsername("admin")
    hikariConfig.setPassword("root")
    new HikariDataSource(hikariConfig)
  }

  val calendarProject = ZLayer.make[Server](
    NoteDao.live,
    ZLayer.succeed(
      datasource
    ) >>> ConnectionSource.fromDatasource >>> Database.fromConnectionSource >>> NoteService.live,
    CalendarImpl.live,
    serverLayer
  )

  def run =
    (calendarProject.build *> ZIO.never).exitCode
}
