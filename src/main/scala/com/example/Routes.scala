package com.example

import java.sql.{SQLClientInfoException, SQLException}

import com.example.model._

import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import scalikejdbc.SQLExecution

trait Routes extends JsonSupport {
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case ex: UserNotFound => complete(NotFound -> ex.getMessage)
    case ex: UserAlreadyExists => complete(Conflict -> ex.getMessage)
    case ex: SQLException => complete(InternalServerError -> ex.getMessage)
  }

  def logMessage(message: String): String = {
    log.info(message)
    message
  }

  lazy val userRoutes: Route =
    pathPrefix("users") {
      pathEndOrSingleSlash {
        get {
          complete(DataBase.getUsers)
        } ~
          post {
            withRequestTimeout(10.seconds) {
              entity(as[UserOutput]) { user =>
                onSuccess(DataBase.addUser(user)) {
                  complete(Created -> logMessage(s"User [${user.name}] created."))
                }
              }
            }
          }
      } ~
        path(Segment) { name =>
          get {
            complete(DataBase.getUser(name))
            /*onSuccess(Database.getUser(name)) { user =>
              complete(OK -> user)
            }*/
          } ~
            delete {
              onSuccess(DataBase.removeUser(name)) {
                complete(OK -> logMessage(s"User [$name] deleted."))
              }
            } ~
            put {
              withRequestTimeout(10.seconds) {
                entity(as[UserOutput]) { user =>
                  onSuccess(DataBase.editUser(name, user)) {
                    complete(OK -> logMessage(s"User [$name] updated."))
                  }
                }
              }
            }
        }
    } ~
      pathPrefix("transfers") {
        pathEndOrSingleSlash {
          get {
            complete(DataBase.getTransfers)
          } ~
            post {
              withRequestTimeout(10.seconds) {
                entity(as[TransferOutput]) { transfer =>
                  onSuccess(DataBase.makeTransfer(transfer)) {
                    complete(Created -> logMessage(s"Transfer [${transfer.from} to {transfer.to}] complete."))
                  }
                }
              }
            }
        } ~
          path(Segment) { name =>
            get {
              complete(DataBase.getTransfers(name))
            }
          }
      }

}
