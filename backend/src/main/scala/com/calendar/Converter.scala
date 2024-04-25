package com.calendar
import com.calendar.model.Note
import calendar.calendar.{Note => ProtoNote}

object Converter {
  def toProto(note: Note): ProtoNote = {
    ProtoNote(note.id, note.text, note.userId, note.date)
  }
}
