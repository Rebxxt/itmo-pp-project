package com.calendar.model

case class User(login: String)

object User {
  def fromUserSource(userSource: UserSource): User = {
    User(login = userSource.login)
  }
}
