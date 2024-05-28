package com.calendar.model
import org.joda.time.DateTime

case class Note(id: String, text: String, userId: String, date: Long)

object Note {
  def fromNoteSource(noteSource: NoteSource, id: String): Note =
    Note(
      id = id,
      text = noteSource.text,
      userId = noteSource.userId,
      date = noteSource.date
    )
}
