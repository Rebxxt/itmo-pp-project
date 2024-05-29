package com.calendar.api.http
import akka.http.scaladsl.server.{Directive1, Route}
trait Handler {
  def route: Route
}
