package com.calendar.api

import calendar.calendar.ZioCalendar.Calendar
import calendar.calendar._
import com.calendar.Converter
import com.calendar.alert.AlertBot
import com.calendar.model.{NoteSource, UserSource}
import com.calendar.service.{AuthService, NoteService, UserService}
import io.grpc.{Status, StatusException}
import zio._

class CalendarImpl(
    noteService: NoteService,
    userService: UserService,
    authService: AuthService,
    alertBot: AlertBot
) extends Calendar {
  override def createNote(
      request: AddNoteRequest
  ): IO[
    StatusException,
    AddNoteResponse
  ] = {
    val note = NoteSource(
      text = request.text,
      userLogin = request.userLogin,
      date = request.date
    )
    noteService
      .addNote(note)
      .tapError(t =>
        alertBot
          .alert(
            s"ALERT: Exception occurred while running CalendarImpl.createNote, message: ${t.getMessage}"
          )
          .orElseFail(new StatusException(Status.INTERNAL))
      )
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
      .tapError(t =>
        alertBot
          .alert(
            s"ALERT: Exception occurred while running CalendarImpl.getNote, message: ${t.getMessage}"
          )
          .orElseFail(new StatusException(Status.INTERNAL))
      )
      .mapBoth(
        _ => new StatusException(Status.INTERNAL),
        note => GetNoteResponse(note.map(Converter.toProto))
      )

  override def getUserNotes(
      request: GetUserNotesRequest
  ): IO[
    StatusException,
    GetUserNotesResponse
  ] = noteService
    .getUserNotes(request.userLogin)
    .tapError(t =>
      alertBot
        .alert(
          s"ALERT: Exception occurred while running CalendarImpl.getUserNotes, message: ${t.getMessage}"
        )
        .orElseFail(new StatusException(Status.INTERNAL))
    )
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
    .tapError(t =>
      alertBot
        .alert(
          s"ALERT: Exception occurred while running CalendarImpl.deleteNote, message: ${t.getMessage}"
        )
        .orElseFail(new StatusException(Status.INTERNAL))
    )
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
    .createUser(UserSource(request.userLogin, request.password))
    .tapError(t =>
      alertBot
        .alert(
          s"ALERT: Exception occurred while running CalendarImpl.createUser, message: ${t.getMessage}"
        )
        .orElseFail(new StatusException(Status.INTERNAL))
    )
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
    .deleteUser(request.userLogin)
    .tapError(t =>
      alertBot
        .alert(
          s"ALERT: Exception occurred while running CalendarImpl.deleteUser, message: ${t.getMessage}"
        )
        .orElseFail(new StatusException(Status.INTERNAL))
    )
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
    .getUser(request.userLogin)
    .tapError(t =>
      alertBot
        .alert(
          s"ALERT: Exception occurred while running CalendarImpl.getUser, message: ${t.getMessage}"
        )
        .orElseFail(new StatusException(Status.INTERNAL))
    )
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      user => GetUserResponse(user.map(Converter.toProto))
    )

  override def authenticateUser(
      request: AuthenticateUserRequest
  ): IO[
    StatusException,
    AuthenticateUserResponse
  ] = authService
    .authenticateUser(request.login, request.password)
    .tapError(t =>
      alertBot
        .alert(
          s"ALERT: Exception occurred while running CalendarImpl.authenticateUser, message: ${t.getMessage}"
        )
        .orElseFail(new StatusException(Status.INTERNAL))
    )
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
    .deleteAuth(request.userLogin)
    .tapError(t =>
      alertBot
        .alert(
          s"ALERT: Exception occurred while running CalendarImpl.deleteUser, message: ${t.getMessage}"
        )
        .orElseFail(new StatusException(Status.INTERNAL))
    )
    .mapBoth(
      _ => new StatusException(Status.INTERNAL),
      _ => DeleteAuthResponse()
    )
}

object CalendarImpl {
  def live: ZLayer[
    NoteService with UserService with AuthService with AlertBot,
    Throwable,
    CalendarImpl
  ] = {
    ZLayer.fromFunction(new CalendarImpl(_, _, _, _))
  }
}
