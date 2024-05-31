package com.calendar.api.http
import akka.http.interop.ZIOSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{
  complete,
  delete,
  get,
  parameter,
  post
}
import akka.http.scaladsl.server.{Directive1, Route}
import com.calendar.alert.AlertBot
import com.calendar.model.{Note, NoteSource, User, UserSource}
import com.calendar.service.{NoteService, UserService}
import akka.http.scaladsl.server.directives.PathDirectives._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import akka.http.scaladsl.server.RouteConcatenation._
import com.calendar.api.http.UserHandler.JsonSupport
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.{DELETE, GET, POST, Path}

@Path("/user")
class UserHandler(userService: UserService, alertBot: AlertBot)
    extends ZIOSupport
    with JsonSupport
    with Handler {

  override def route: Route = createUser ~ getUser ~ deleteUser

  @POST
  @Operation(
    summary = "Create new user",
    description = "Creates and returns new user",
    parameters = Array(
      new Parameter(
        name = "user_login",
        in = ParameterIn.QUERY,
        description = "User login",
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
  @ApiResponse(responseCode = "200", description = "Created user")
  def createUser: Route =
    (post & WithUserLogin & WithPassword & pathEndOrSingleSlash) {
      (userLogin, password) =>
        complete(
          userService.createUser(
            UserSource(login = userLogin, password = password)
          )
        )
    }

  @GET
  @Operation(
    summary = "Get user",
    description = "Get user by user login",
    parameters = Array(
      new Parameter(
        name = "user_login",
        in = ParameterIn.QUERY,
        description = "User id",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      )
    )
  )
  @ApiResponse(responseCode = "200", description = "User")
  def getUser: Route =
    (get & WithUserLogin & pathEndOrSingleSlash) { userLogin =>
      complete(userService.getUser(userLogin = userLogin))
    }

  @DELETE
  @Operation(
    summary = "Delete user",
    description = "Delete user by user login",
    parameters = Array(
      new Parameter(
        name = "user_login",
        in = ParameterIn.QUERY,
        description = "User login",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      )
    )
  )
  @ApiResponse(responseCode = "200", description = "OK if user was deleted")
  def deleteUser: Route =
    (delete & WithUserLogin & pathEndOrSingleSlash) { userLogin =>
      complete(userService.deleteUser(userLogin = userLogin).as("OK"))
    }

  private val WithUserLogin: Directive1[String] = parameter(
    Symbol("user_login").as[String]
  )

  private val WithPassword: Directive1[String] = parameter(
    Symbol("password").as[String]
  )
}
object UserHandler {
  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val userFormat: RootJsonFormat[User] = jsonFormat1(User.apply)
  }
}
