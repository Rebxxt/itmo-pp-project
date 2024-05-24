package com.calendar
import com.calendar.model.{Note, User}
import calendar.calendar.{Note => ProtoNote, User => ProtoUser}

object Converter {
  def toProto(note: Note): ProtoNote = {
    ProtoNote(
      noteId = note.id,
      text = note.text,
      userId = note.userId,
      date = note.date
    )
  }

  def toProto(user: User): ProtoUser = {
    ProtoUser(userId = user.id, userName = user.name)
  }
}
