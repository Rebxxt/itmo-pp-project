package com.calendar.api

import calendar.calendar.ZioCalendar.Calendar
import calendar.calendar._
import com.calendar.Converter
import com.calendar.model.{NoteSource, UserSource}
import com.calendar.service.{AuthService, NoteService, UserService}
import io.grpc.{Status, StatusException}
import zio._

class CalendarImpl(
    noteService: NoteService,
    userService: UserService,
    authService: AuthService
) extends Calendar {
  override def createNote(
      request: AddNoteRequest
  ): IO[
    StatusException,
    AddNoteResponse
  ] = {
    val note = NoteSource(request.text, request.userId, request.date)
    noteService
      .addNote(note)
      .mapBoth(
        _ => new StatusException(Status.INTERNAL),
        note => AddNoteResponse(Some(Converter.toProto(note)))
      )
  }

  override def getNote(
      request: GetNoteRequest
  ): IO[
    StatusException,
    GetNoteResponse
  ] =
    noteService
      .getNote(request.noteId)
      .mapBoth(
        _ => new StatusException(Status.INTERNAL),
        note => GetNoteResponse(Some(Converter.toProto(note)))
      )

  override def getUserNotes(
      request: GetUserNotesRequest
  ): IO[
    StatusException,
    GetUserNotesResponse
  ] = noteService
    .getUserNotes(request.userId)
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      notes => GetUserNotesResponse(notes.map(Converter.toProto))
    )

  override def deleteNote(
      request: DeleteNoteRequest
  ): IO[
    StatusException,
    DeleteNoteResponse
  ] = noteService
    .deleteNode(request.noteId)
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      _ => DeleteNoteResponse()
    )

  override def createUser(
      request: CreateUserRequest
  ): IO[
    StatusException,
    CreateUserResponse
  ] = userService
    .createUser(UserSource(request.userName, request.password))
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      user => CreateUserResponse(user = Some(Converter.toProto(user)))
    )

  override def deleteUser(
      request: DeleteUserRequest
  ): IO[
    StatusException,
    DeleteUserResponse
  ] = userService
    .deleteUser(request.userId)
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      _ => DeleteUserResponse()
    )
  override def getUser(
      request: GetUserRequest
  ): IO[
    StatusException,
    GetUserResponse
  ] = userService
    .getUser(request.userId)
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      user => GetUserResponse(Some(Converter.toProto(user)))
    )
  override def authenticateUser(
      request: AuthenticateUserRequest
  ): IO[
    StatusException,
    AuthenticateUserResponse
  ] = authService
    .authenticateUser(request.userId, request.password)
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      result => AuthenticateUserResponse(result = result)
    )

  override def deleteAuth(
      request: DeleteAuthRequest
  ): IO[
    StatusException,
    DeleteAuthResponse
  ] = authService
    .deleteAuth(request.id)
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      _ => DeleteAuthResponse()
    )
}

object CalendarImpl {
  def live: ZLayer[
    NoteService with UserService with AuthService,
    Throwable,
    CalendarImpl
  ] = {
    ZLayer.fromFunction(new CalendarImpl(_, _, _))
  }
}
