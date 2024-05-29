package com.calendar.api.http
import akka.http.interop.ZIOSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{
  complete,
  delete,
  parameter,
  pathEndOrSingleSlash,
  post
}
import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.interop.ZIOSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, parameter}
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
import jakarta.ws.rs.{DELETE, GET, POST, Path}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

@Path("/note")
class NoteHandler(
    noteService: NoteService,
    alertBot: AlertBot
) extends ZIOSupport
    with JsonSupport
    with Handler {

  override def route: Route = createNote ~ getUserNotes ~ deleteNote

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
        name = "user_id",
        in = ParameterIn.QUERY,
        description = "User id of note owner",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String]))
        )
      ),
      new Parameter(
        name = "date",
        in = ParameterIn.QUERY,
        description = "Date of note creation",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[Long]))
        )
      )
    )
  )
  @ApiResponse(responseCode = "200", description = "Created note")
  def createNote: Route =
    (post & WithText & WithUserId & WithDate & pathEndOrSingleSlash) {
      (text, userId, date) =>
        complete(noteService.addNote(NoteSource(text, userId, date)))
    }

  @GET
  @Operation(
    summary = "Get user notes",
    description = "Get user notes by user id",
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
  @ApiResponse(responseCode = "200", description = "List of user notes")
  def getUserNotes: Route = (get & WithUserId & pathEndOrSingleSlash) {
    userId =>
      complete(noteService.getUserNotes(userId))
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
  private val WithUserId: Directive1[String] = parameter(
    Symbol("user_id").as[String]
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
