package com.calendar.api.http
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.calendar.alert.AlertBot
import akka.http.scaladsl.server.Directive._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import akka.http.scaladsl.server.directives.PathDirectives._
import api.SwaggerDocService
import com.calendar.service.{AuthService, NoteService, UserService}
import zio.{Task, ZIO, ZLayer}

class ApiHandler(
    noteService: NoteService,
    userService: UserService,
    authService: AuthService,
    alertBot: AlertBot
) {
  def route: Route = {
    pathPrefix("note") {
      new NoteHandler(noteService, alertBot).route
    } ~ pathPrefix("user") {
      new UserHandler(userService, alertBot).route
    } ~ pathPrefix("auth") {
      new AuthHandler(authService, alertBot).route
    } ~ SwaggerDocService.routes
  }

  def run: Task[Unit] = {
    implicit val system: ActorSystem = ActorSystem("calendar")
    Http().newServerAt("0.0.0.0", 8080).bind(route)
    ZIO.never
  }
}

object ApiHandler {
  def live: ZLayer[
    NoteService with UserService with AuthService with AlertBot,
    Throwable,
    ApiHandler
  ] = {
    ZLayer.fromFunction(new ApiHandler(_, _, _, _))
  }
}
