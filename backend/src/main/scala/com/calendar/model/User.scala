package com.calendar.model

case class User(id: String, name: String)

object User {
  def fromUserSource(userSource: UserSource, id: String): User = {
    User(id = id, name = userSource.name)
  }
}
