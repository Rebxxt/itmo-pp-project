package scala.com.calendar
import com.calendar.service.runTransaction
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import org.testcontainers.containers.wait.strategy.{Wait, WaitAllStrategy}
import org.testcontainers.utility.DockerImageName
import zio.interop.catz.asyncInstance
import zio.{Task, ZIO}

import scala.io.Source

package object storage {

  private def readSql(path: String): ZIO[Any, Throwable, String] = {
    ZIO.scoped {
      ZIO
        .fromAutoCloseable(
          ZIO.attempt(
            getClass
              .getResourceAsStream(path)
              .ensuring(_ != null, s"Schema path $path not found")
          )
        )
        .flatMap(is => ZIO.attempt(Source.fromInputStream(is).mkString))
    }
  }

  def initSchema(path: String, transactor: Transactor[Task]): Task[Unit] =
    for {
      schema <- readSql(path)
      _ <- runTransaction(Update0(schema, None).run)(transactor)
    } yield ()

  def prepareContainer: ZIO[Any, Throwable, Transactor[Task]] = {
    ZIO
      .succeed(
        new PostgreSQLContainer(
          dockerImageNameOverride =
            Some(DockerImageName.parse("postgres").withTag("14.3-alpine")),
          databaseName = Some("calendar"),
          mountPostgresDataToTmpfs = true
        )
      )
      .tap(c =>
        ZIO.succeed(
          c.container.setWaitStrategy(
            new WaitAllStrategy()
              .withStrategy(Wait.forListeningPort())
              .withStrategy(
                Wait.forLogMessage(
                  ".*database system is ready to accept connections.*",
                  1
                )
              )
          )
        )
      )
      .tap(c => ZIO.attempt(c.start()))
      .map(container =>
        Transactor.fromDriverManager[Task](
          container.driverClassName,
          container.jdbcUrl,
          container.username,
          container.password
        )
      )
      .tap(initSchema("/db.sql", _))
  }
}
