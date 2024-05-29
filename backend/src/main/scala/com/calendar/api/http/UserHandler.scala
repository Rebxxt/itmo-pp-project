package com.calendar.api.http
import akka.http.interop.ZIOSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, delete, get, parameter, post}
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
  @ApiResponse(responseCode = "200", description = "Created user")
  def createUser: Route =
    (post & WithUserName & WithPassword & pathEndOrSingleSlash) {
      (userName, password) =>
        complete(userService.createUser(UserSource(userName, password)))
    }

  @GET
  @Operation(
    summary = "Get user",
    description = "Get user by user id",
    parameters = Array(
      new Parameter(
        name = "user_id",
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
    (get & WithUserId & pathEndOrSingleSlash) { userId =>
      complete(userService.getUser(userId))
    }

  @DELETE
  @Operation(
    summary = "Delete user",
    description = "Delete user by user id",
    parameters = Array(
      new Parameter(
        name = "user_id",
        in = ParameterIn.QUERY,
        description = "User id",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      )
    )
  )
  @ApiResponse(responseCode = "200", description = "OK if user was deleted")
  def deleteUser: Route =
    (delete & WithUserId & pathEndOrSingleSlash) { userId =>
      complete(userService.deleteUser(userId).as("OK"))
    }

  private val WithUserName: Directive1[String] = parameter(
    Symbol("user_name").as[String]
  )

  private val WithUserId: Directive1[String] = parameter(
    Symbol("user_id").as[String]
  )

  private val WithPassword: Directive1[String] = parameter(
    Symbol("password").as[String]
  )
}
object UserHandler {
  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User.apply)
  }
}
