package com.calendar.api.http
import akka.http.interop.ZIOSupport
import akka.http.scaladsl.server.Directives.{complete, parameter, post}
import akka.http.scaladsl.server.{Directive1, Route}
import com.calendar.alert.AlertBot
import akka.http.scaladsl.server.directives.PathDirectives._
import com.calendar.model.UserSource
import com.calendar.service.{AuthService, UserService}
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.{POST, Path}

@Path("/auth")
class AuthHandler(authService: AuthService, alertBot: AlertBot)
    extends ZIOSupport
    with Handler {

  override def route: Route = authenticateUser

  @POST
  @Operation(
    summary = "Authenticate user",
    description = "Tries to authenticate user by login and password",
    parameters = Array(
      new Parameter(
        name = "user_name",
        in = ParameterIn.QUERY,
        description = "User name",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      ),
      new Parameter(
        name = "password",
        in = ParameterIn.QUERY,
        description = "User password",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      )
    )
  )
  @ApiResponse(
    responseCode = "200",
    description = "True if authentication succeeded, false otherwise"
  )
  def authenticateUser: Route =
    (post & WithUserName & WithPassword & pathEndOrSingleSlash) {
      (userName, password) =>
        complete(
          authService.authenticateUser(userName, password).map(_.toString)
        )
    }

  private val WithUserName: Directive1[String] = parameter(
    Symbol("user_name").as[String]
  )

  private val WithPassword: Directive1[String] = parameter(
    Symbol("password").as[String]
  )

}