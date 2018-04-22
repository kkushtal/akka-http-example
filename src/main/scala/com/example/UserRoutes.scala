package com.example

import com.example.model._
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{ExceptionHandler, Route}


trait UserRoutes extends JsonSupport {
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[UserRoutes])

  implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case ex: UserNotFound => complete(NotFound -> ex.getMessage)
    case ex: UserAlreadyExists => complete(Conflict -> ex.getMessage)
  }

  def logMessage(message: String): String = {
    log.info(message)
    message
  }

  lazy val userRoutes: Route =
    pathPrefix("users") {
      pathEndOrSingleSlash {
        get {
          complete(UserRepository.selectAll)
        } ~
          post {
            withRequestTimeout(10.seconds) {
              entity(as[User]) { user =>
                onSuccess(UserRepository.insert(user)) {
                  complete(Created -> logMessage(s"User [${user.name}] created."))
                }
              }
            }
          } ~
          path(Segment) { name =>
            get {
              onSuccess(UserRepository.select(name)) { user =>
                complete(OK -> user)
              }
            } ~
              delete {
                onSuccess(UserRepository.delete(name)) { user =>
                  complete(OK -> logMessage(s"User [${user.name}] deleted."))
                }
              } ~
              put {
                withRequestTimeout(10.seconds) {
                  entity(as[User]) { newUser =>
                    onSuccess(UserRepository.update(name, newUser)) { oldUser =>
                      complete(OK -> logMessage(s"User [${oldUser.name}] updated."))
                    }
                  }
                }
              }
          }
      }
    }
}
