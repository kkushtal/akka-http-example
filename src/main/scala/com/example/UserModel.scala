package com.example.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/* ********** USER MODEL ********** */
final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])


/* ********** ERRORS ********** */
final case class UserAlreadyExists(private val name: String,
                                   private val cause: Throwable = None.orNull)
  extends Exception(s"User [$name] already exists.", cause)


final case class UserNotFound(private val name: String,
                              private val cause: Throwable = None.orNull)
  extends Exception(s"User [$name] not found.", cause)


/* ********** JSON SUPPORT ********** */
trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
}