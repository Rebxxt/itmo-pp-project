package com.calendar.service

import com.calendar.model.{Note, NoteSource}
import com.calendar.storage.NoteDao
import io.github.gaelrenoux.tranzactio.DbException
import io.github.gaelrenoux.tranzactio.doobie.Database
import zio.{ZIO, ZLayer}

class NoteService(noteDao: NoteDao, database: Database) {
  def getNote(): ZIO[Any, Either[DbException, DbException], Note] = {
    Database
      .transaction(noteDao.getNote())
      .provideLayer(ZLayer.succeed(database))
  }

  def addNote(
      noteSource: NoteSource
  ): ZIO[Any, Either[DbException, DbException], Unit] = {
    Database
      .transaction(noteDao.addNote(noteSource))
      .provideLayer(ZLayer.succeed(database))
  }

  def getUserNotes(
      userId: Long
  ): ZIO[Any, Either[DbException, DbException], Seq[Note]] = {
    Database
      .transaction(noteDao.getUserNotes(userId))
      .provideLayer(ZLayer.succeed(database))
  }

  def deleteNode(
      noteId: Long
  ): ZIO[Any, Either[DbException, DbException], Unit] = {
    Database
      .transaction(noteDao.deleteNote(noteId))
      .provideLayer(ZLayer.succeed(database))
  }
}

object NoteService {
  def live: ZLayer[NoteDao with Database, Throwable, NoteService] =
    ZLayer.fromFunction(new NoteService(_, _))
}
