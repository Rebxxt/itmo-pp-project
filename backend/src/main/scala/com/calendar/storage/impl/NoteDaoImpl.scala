package com.calendar.storage.impl
import cats.MonadError
import cats.implicits.catsSyntaxApplicativeId
import com.calendar.model.{Note, NoteSource}
import com.calendar.storage.NoteDao
import doobie.implicits.toSqlInterpolator
import doobie.free.connection.ConnectionIO

object NoteDaoImpl extends NoteDao {
  override def addNote(note: Note): ConnectionIO[Note] = {
    val text = note.text
    val userLogin = note.userLogin
    val date = note.date
    val id = note.id
    sql"insert into notes(id, text, user_login, date) values ($id, $text, $userLogin, $date)".update.run
      .map(_ => note)
  }
  override def getNote(noteId: String): ConnectionIO[Option[Note]] = {
    sql"select id, text, user_login, date from notes where id = $noteId"
      .query[Note]
      .option
  }
  override def getUserNotes(
      userLogin: String
  ): ConnectionIO[Seq[Note]] =
    sql"select id, text, user_login, date from notes where user_login = $userLogin"
      .query[Note]
      .to[Seq] // :TODO fix

  override def deleteNote(noteId: String): ConnectionIO[Unit] =
    sql"delete from notes where id = $noteId".update.run.map(_ => ())

  override def updateNote(
      note: Note
  ): ConnectionIO[
    Note
  ] = {
    val text = note.text
    val userLogin = note.userLogin
    val date = note.date
    val id = note.id
    sql"update notes set text = $text, user_login = $userLogin, date = $date where id = $id".update.run
      .flatMap {
        case 1 => note.pure[ConnectionIO]
        case _ =>
          MonadError[ConnectionIO, Throwable].raiseError(
            new Exception("Update operation did not affect exactly one row")
          )
      }

  }
}
