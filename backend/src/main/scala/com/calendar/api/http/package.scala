package com.calendar.api
import akka.http.scaladsl.model.{HttpMethods, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives.{complete, options, respondWithHeaders}
import akka.http.scaladsl.server.{Directive0, Route}

package object http {

  def addAccessControlHeaders: Directive0 = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`.*,
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "X-Custom-Header"
      )
    )
  }

  def preflightRequestHandler: Route = options {
    complete(
      HttpResponse(StatusCodes.OK).withHeaders(
        `Access-Control-Allow-Methods`(
          HttpMethods.OPTIONS,
          HttpMethods.POST,
          HttpMethods.PUT,
          HttpMethods.GET,
          HttpMethods.DELETE
        )
      )
    )
  }

}
