package com.calendar.storage

import com.calendar.model.{Note, NoteSource}
import zio._
import doobie.implicits._
import com.calendar.storage.impl.NoteDaoImpl
import doobie.free.connection.ConnectionIO

trait NoteDao {
  def addNote(note: Note): ConnectionIO[Note]
  def getNote(noteId: String): ConnectionIO[Note]
  def getUserNotes(userId: String): ConnectionIO[Seq[Note]]
  def deleteNote(noteId: String): ConnectionIO[Unit]
}

object NoteDao {
  def live: ZLayer[Any, Nothing, NoteDao] = ZLayer.succeed(NoteDaoImpl)
}
