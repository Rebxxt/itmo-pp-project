package com.calendar.storage.impl
import com.calendar.model.{Note, NoteSource}
import com.calendar.storage.NoteDao
import doobie.implicits.toSqlInterpolator
import doobie.free.connection.ConnectionIO

object NoteDaoImpl extends NoteDao {
  override def addNote(note: Note): ConnectionIO[Note] = {
    val text = note.text
    val userId = note.userId
    val date = note.date
    val id = note.id
    sql"insert into notes(id, text, user_id, date) values ($id, $text, $userId, $date)".update.run
      .map(_ => note)
  }
  override def getNote(noteId: String): ConnectionIO[Option[Note]] = {
    sql"select id, text, user_id, date from notes where id = $noteId"
      .query[Note]
      .option
  }
  override def getUserNotes(
      userId: String
  ): ConnectionIO[Seq[Note]] =
    sql"select id, text, user_id, date from notes where user_id = $userId"
      .query[Note]
      .to[Seq] // :TODO fix

  override def deleteNote(noteId: String): ConnectionIO[Unit] =
    sql"delete from notes where id = $noteId".update.run.map(_ => ())
}
