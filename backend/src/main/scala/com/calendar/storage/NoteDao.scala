package com.calendar.storage

import com.calendar.model.{Note, NoteSource}
import zio._
import doobie.implicits._
import io.github.gaelrenoux.tranzactio._
import io.github.gaelrenoux.tranzactio.doobie._
import com.calendar.storage.impl.NoteDaoImpl

trait NoteDao {
  def addNote(note: NoteSource): ZIO[Connection, DbException, Unit]
  def getNote(): ZIO[Connection, DbException, Note]
  def getUserNotes(userId: Long): ZIO[Connection, DbException, Seq[Note]]
  def deleteNote(noteId: Long): ZIO[Connection, DbException, Unit]
}

object NoteDao {
  def live: ZLayer[Any, Nothing, NoteDao] = ZLayer.succeed(NoteDaoImpl)
}
