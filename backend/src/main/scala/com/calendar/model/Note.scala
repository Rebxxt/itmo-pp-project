package com.calendar.model

case class Note(id: String, text: String, userLogin: String, date: Long)

object Note {
  def fromNoteSource(noteSource: NoteSource, id: String): Note =
    Note(
      id = id,
      text = noteSource.text,
      userLogin = noteSource.userLogin,
      date = noteSource.date
    )
}
