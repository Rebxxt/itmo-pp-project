package com.calendar
import doobie.free.connection.ConnectionIO
import doobie.util.transactor
import doobie.util.transactor.Transactor
import zio.interop.catz._
import zio.{Task, ZIO, ZLayer}

import zio.Task

package object service {
  def runTransaction[A](connection: ConnectionIO[A])(implicit
      transactor: Transactor[Task]
  ): Task[A] =
    transactor.trans.apply(connection)
}
