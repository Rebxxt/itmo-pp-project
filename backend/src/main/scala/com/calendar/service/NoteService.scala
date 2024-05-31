package com.calendar.service

import com.calendar.UUID
import com.calendar.model.{Note, NoteSource}
import com.calendar.storage.impl.NoteDaoImpl
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor
import zio.interop.catz._
import zio.{Task, ZIO, ZLayer}

class NoteService(transactor: Transactor[Task]) {
  implicit val tr: Transactor[Task] = transactor

  def getNote(noteId: String): Task[Option[Note]] = runTransaction(
    NoteDaoImpl.getNote(noteId)
  )

  def addNote(
      noteSource: NoteSource
  ): ZIO[Any, Throwable, Note] = {
    for {
      id <- UUID.generateUUID
      note <- runTransaction(
        NoteDaoImpl.addNote(Note.fromNoteSource(noteSource, id))
      )
    } yield note
  }

  def updateNote(note: Note): ZIO[Any, Throwable, Note] = {
    runTransaction(NoteDaoImpl.updateNote(note))
  }

  def getUserNotes(
      userLogin: String
  ): ZIO[Any, Throwable, Seq[Note]] = {
    runTransaction(NoteDaoImpl.getUserNotes(userLogin))
  }

  def deleteNode(noteId: String): ZIO[Any, Throwable, Unit] = {
    runTransaction(NoteDaoImpl.deleteNote(noteId))
  }
}

object NoteService {
  def live: ZLayer[Transactor[Task], Throwable, NoteService] =
    ZLayer.fromFunction(new NoteService(_))
}
