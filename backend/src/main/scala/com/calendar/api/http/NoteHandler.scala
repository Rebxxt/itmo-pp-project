package com.calendar.api.http
import akka.http.interop.ZIOSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, delete, options, parameter, pathEndOrSingleSlash, post, respondWithHeaders}
import akka.http.scaladsl.server.{Directive0, Directive1, Route}
import akka.http.scaladsl.server.directives.MethodDirectives.{get, put}
import akka.http.interop.ZIOSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpMethods, StatusCodes}
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.server.RouteConcatenation._
import akka.http.scaladsl.server.directives.PathDirectives._
import akka.http.scaladsl.server.directives.PathDirectives.pathPrefix
import com.calendar.alert.AlertBot
import com.calendar.api.http.NoteHandler.JsonSupport
import com.calendar.model.{Note, NoteSource}
import com.calendar.service.{AuthService, NoteService, UserService}
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.{DELETE, GET, POST, PUT, Path}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import akka.http.scaladsl.model.HttpResponse




@Path("/note")
class NoteHandler(
    noteService: NoteService,
    alertBot: AlertBot
) extends ZIOSupport
    with JsonSupport
    with Handler {

  override def route: Route =
    addAccessControlHeaders {
      preflightRequestHandler ~ createNote ~ getUserNotes ~ deleteNote ~ updateNote
    }

  @POST
  @Operation(
    summary = "Create new note",
    description = "Creates and returns new note",
    parameters = Array(
      new Parameter(
        name = "text",
        in = ParameterIn.QUERY,
        description = "Note text",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      ),
      new Parameter(
        name = "user_login",
        in = ParameterIn.QUERY,
        description = "User login of note owner",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      ),
      new Parameter(
        name = "date",
        in = ParameterIn.QUERY,
        description = "Note date",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[Long]))
        )
      )
    )
  )
  @ApiResponse(responseCode = "200", description = "Created note")
  def createNote: Route =
    (post & WithText & WithUserLogin & WithDate & pathEndOrSingleSlash) {
      (text, userLogin, date) =>
        complete(noteService.addNote(NoteSource(text, userLogin, date)))
    }

  @PUT
  @Operation(
    summary = "Update note",
    description = "Updates note",
    parameters = Array(
      new Parameter(
        name = "note_id",
        in = ParameterIn.QUERY,
        description = "Note id",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      ),
      new Parameter(
        name = "text",
        in = ParameterIn.QUERY,
        description = "Note text",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      ),
      new Parameter(
        name = "user_login",
        in = ParameterIn.QUERY,
        description = "User login of note owner",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      ),
      new Parameter(
        name = "date",
        in = ParameterIn.QUERY,
        description = "Note date",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[Long]))
        )
      )
    )
  )
  @ApiResponse(responseCode = "200", description = "Updated note")
  def updateNote: Route =
    (put & WithNoteId & WithText & WithUserLogin & WithDate & pathEndOrSingleSlash) {
      (noteId, text, userLogin, date) =>
        complete(
          noteService.updateNote(
            Note(id = noteId, text = text, userLogin = userLogin, date = date)
          )
        )
    }

  @GET
  @Operation(
    summary = "Get user notes",
    description = "Get user notes by user login",
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
  @ApiResponse(responseCode = "200", description = "List of user notes")
  def getUserNotes: Route = (get & WithUserLogin & pathEndOrSingleSlash) {
    userLogin =>
      complete(noteService.getUserNotes(userLogin = userLogin))
  }

  @DELETE
  @Operation(
    summary = "Delete note",
    description = "Delete note by note id",
    parameters = Array(
      new Parameter(
        name = "note_id",
        in = ParameterIn.QUERY,
        description = "Note id",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      )
    )
  )
  @ApiResponse(responseCode = "200", description = "OK if note was deleted")
  def deleteNote: Route = (delete & WithNoteId & pathEndOrSingleSlash) {
    noteId => complete(noteService.deleteNode(noteId).as("OK"))
  }

  private val WithText: Directive1[String] = parameter(
    Symbol("text").as[String]
  )
  private val WithUserLogin: Directive1[String] = parameter(
    Symbol("user_login").as[String]
  )

  private val WithNoteId: Directive1[String] = parameter(
    Symbol("note_id").as[String]
  )

  private val WithDate: Directive1[Long] = parameter(
    Symbol("date").as[Long]
  )
}

object NoteHandler {
  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val noteFormat: RootJsonFormat[Note] = jsonFormat4(Note.apply)

  }
}
