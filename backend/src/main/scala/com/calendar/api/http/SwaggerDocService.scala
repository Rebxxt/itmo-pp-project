package com.calendar.api.http

package api
import com.github.swagger.akka.model.Info
import com.github.swagger.akka.ui.SwaggerHttpWithUiService

object SwaggerDocService extends SwaggerHttpWithUiService {
  override val apiClasses: Set[Class[_]] =
    Set(classOf[NoteHandler], classOf[UserHandler], classOf[AuthHandler])
  override val host: String =
    "localhost:8080" // Ensure this matches your actual host and port
  override val schemes: List[String] = List("http")
  override def apiDocsPath: String = "docs"

  override val info: Info = Info(
    title = "Forecast Project Api",
    version = "1.0",
    description = "API documentation for the Forecast Project Api"
  )
}
