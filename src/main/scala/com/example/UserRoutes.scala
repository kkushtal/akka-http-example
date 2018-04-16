package com.example

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Route, ExceptionHandler}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import java.sql.SQLException

import com.example.UserActor._

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
}

trait UserRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[UserRoutes])

  def userActor: ActorRef

  val exceptionHandler = ExceptionHandler {
    case ex: SQLException => complete(StatusCodes.Conflict, ex.getMessage)
  }

  implicit lazy val timeout = Timeout(5.seconds)

  lazy val userRoutes: Route =
    pathPrefix("users") {
      pathEndOrSingleSlash {
        get {
          complete {
            (userActor ? GetUsers).mapTo[Users]
          }
        } ~
          post {
            withRequestTimeout(10.seconds) {
              handleExceptions(exceptionHandler) {
                entity(as[User]) { user =>
                  complete {
                    (userActor ? CreateUser(user)) map {
                      case UserCreated(message) => log.info(message); StatusCodes.Created -> message
                      case UserAlreadyExists(message) => StatusCodes.Conflict -> message
                    }
                  }
                }
              }
            }
          }
      } ~
        path(Segment) { name =>
          get {
            onSuccess(userActor ? GetUser(name)) {
              case user: User => complete(StatusCodes.OK -> user)
              case UserNotFound(message) => complete(StatusCodes.NotFound -> message)
            }
          } ~
            delete {
              complete {
                (userActor ? DeleteUser(name)) map {
                  case UserDeleted(message) => log.info(message); StatusCodes.OK -> message
                  case UserNotFound(message) => StatusCodes.NotFound -> message
                }
              }
            } ~
            put {
              withRequestTimeout(10.seconds) {
                handleExceptions(exceptionHandler) {
                  entity(as[User]) { user =>
                    complete {
                      (userActor ? UpdateUser(name)) map {
                        case UserUpdated(message) => log.info(message); StatusCodes.OK -> message
                        case UserNotFound(message) => StatusCodes.NotFound -> message
                      }
                    }
                  }
                }
              }
            }
        }

    }
}
