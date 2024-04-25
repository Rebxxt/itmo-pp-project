package com.calendar.storage.impl
import com.calendar.model.{Note, NoteSource}
import com.calendar.storage.NoteDao
import doobie.implicits.toSqlInterpolator
import io.github.gaelrenoux.tranzactio.DbException
import io.github.gaelrenoux.tranzactio.doobie.tzio
import io.github.gaelrenoux.tranzactio._
import io.github.gaelrenoux.tranzactio.doobie._
import zio.ZIO

object NoteDaoImpl extends NoteDao {
  override def addNote(note: NoteSource): ZIO[Connection, DbException, Unit] = {
    val text = note.text
    val userId = note.userId
    val date = note.date
    tzio {
      sql"insert into notes(text, user_id, date) values ($text, $userId, $date)".update.run
        .map(_ => ())
    }
  }
  override def getNote(): ZIO[Connection, DbException, Note] = {
    tzio {
      sql"select name, latitude, longitude from notes".query[Note].unique
    }
  }
  override def getUserNotes(
      userId: Long
  ): ZIO[Connection, DbException, Seq[Note]] = {
    tzio {
      sql"select text, user_id, date from notes".query[Note].to[Seq]
    }
  }

  override def deleteNote(noteId: Long): ZIO[Connection, DbException, Unit] = {
    tzio {
      sql"delete from notes where id = $noteId".update.run.map(_ => ())
    }
  }
}
