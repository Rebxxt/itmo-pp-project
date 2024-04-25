package com.calendar.api

import calendar.calendar.ZioCalendar.Calendar
import calendar.calendar.{
  AddNoteRequest,
  AddNoteResponse,
  CreateUserRequest,
  CreateUserResponse,
  DeleteNoteRequest,
  DeleteNoteResponse,
  DeleteUserRequest,
  DeleteUserResponse,
  GetNoteRequest,
  GetNoteResponse,
  GetUserNotesRequest,
  GetUserNotesResponse,
  Note => NoteProto
}
import com.calendar.Converter
import com.calendar.model.{Note, NoteSource}
import com.calendar.service.NoteService
import com.calendar.storage.NoteDao
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.github.gaelrenoux.tranzactio.ConnectionSource
import io.github.gaelrenoux.tranzactio.doobie.{Connection, Database}
import io.grpc.{Status, StatusException}
import scalapb.zio_grpc.{ServerMain, ServiceList}
import zio._

class CalendarImpl(noteService: NoteService) extends Calendar {
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
  ] = {
    noteService
      .getNote()
      .mapBoth(
        _ => new StatusException(Status.INTERNAL),
        note => GetNoteResponse(Some(Converter.toProto(note)))
      )
  }
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
  ] = ???

  override def deleteUser(
      request: DeleteUserRequest
  ): IO[
    StatusException,
    DeleteUserResponse
  ] = ???
}

object CalendarImpl {
  def live: ZLayer[NoteService, Throwable, CalendarImpl] = {
    ZLayer.fromFunction(new CalendarImpl(_))
  }
}
