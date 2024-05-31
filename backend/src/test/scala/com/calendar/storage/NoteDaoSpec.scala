package com.calendar.storage
import com.calendar.model.Note
import com.calendar.service.runTransaction
import com.calendar.storage.AuthDaoSpec.{test, testAuth}
import com.calendar.storage.impl.{AuthDaoImpl, NoteDaoImpl}
import doobie.util.transactor.Transactor
import zio.{Scope, Task, ZIO, ZLayer}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
import zio.test._
import zio.test.Assertion._

import scala.com.calendar.storage.prepareContainer

object NoteDaoSpec extends ZIOSpecDefault {
  private val testNote = Note("id", "text", "user id", 2)
  override def spec: Spec[
    TestEnvironment with Scope,
    Any
  ] = {
    test("NoteDao.add") {
      (for {
        noteDao <- ZIO.service[NoteDao]
        transactor <- ZIO.service[Transactor[Task]]
        addedNote <- runTransaction(noteDao.addNote(testNote))(transactor)
        gotNote <- runTransaction(noteDao.getNote(testNote.id))(transactor)
        nonExistingNote <- runTransaction(
          noteDao.getNote(testNote.id + "fake")
        )(transactor)
      } yield assertTrue(
        addedNote == testNote && gotNote.contains(
          testNote
        ) && nonExistingNote.isEmpty
      ))
        .provide(
          ZLayer.succeed(NoteDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    } + test("NoteDao.delete") {
      (for {
        noteDao <- ZIO.service[NoteDao]
        transactor <- ZIO.service[Transactor[Task]]
        _ <- runTransaction(noteDao.addNote(testNote))(transactor)
        _ <- runTransaction(noteDao.deleteNote(testNote.id))(transactor)
        nonExistingNote <- runTransaction(noteDao.getNote(testNote.id))(
          transactor
        )
      } yield assertTrue(nonExistingNote.isEmpty))
        .provide(
          ZLayer.succeed(NoteDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    } + test("NoteDao.add") {
      (for {
        noteDao <- ZIO.service[NoteDao]
        transactor <- ZIO.service[Transactor[Task]]
        testNoteFromSameUser = testNote.copy(
          id = "different id",
          text = "different text",
          date = testNote.date + 1
        )
        _ <- runTransaction(noteDao.addNote(testNote))(transactor)
        _ <- runTransaction(noteDao.addNote(testNoteFromSameUser))(transactor)
        gotNotes <- runTransaction(noteDao.getUserNotes(testNote.userLogin))(
          transactor
        )
      } yield assertTrue(gotNotes.toSet == Set(testNote, testNoteFromSameUser)))
        .provide(
          ZLayer.succeed(NoteDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    } + test("NoteDao.update") {
      (for {
        noteDao <- ZIO.service[NoteDao]
        transactor <- ZIO.service[Transactor[Task]]
        _ <- runTransaction(noteDao.addNote(testNote))(transactor)
        updatedNote = testNote.copy(
          text = "different text",
          userLogin = "different login",
          date = testNote.date + 1
        )
        gotNote <- runTransaction(noteDao.getNote(testNote.id))(transactor)
        _ <- runTransaction(noteDao.updateNote(updatedNote))(transactor)
        gotUpdatedNote <- runTransaction(noteDao.getNote(testNote.id))(
          transactor
        )
      } yield assertTrue(
        gotNote.contains(testNote) && gotUpdatedNote.contains(updatedNote)
      ))
        .provide(
          ZLayer.succeed(NoteDaoImpl),
          ZLayer.fromZIO(prepareContainer)
        )
    }
  }
}
