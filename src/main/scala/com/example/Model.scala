package com.example.model

import java.time.ZonedDateTime

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/* ********** USER MODEL ********** */
case class Transfer(id: Long, amount: Double, fromUserId: Long, toUserId: Long)

//createdAt: ZonedDateTime

case class TransferOutput(amount: Double, from: String, to: String)

case class TransfersOutput(transfers: Seq[TransferOutput])

case class Balance(userId: Long, amount: Double)

case class User(id: Long, name: String, age: Int)

case class UserOutput(name: String, age: Int, balance: Double)

case class UsersOutput(users: Seq[UserOutput])

/* ********** ERRORS ********** */
final case class UserAlreadyExists(
                                    private val name: String,
                                    private val cause: Throwable = None.orNull
                                  )
  extends Exception(s"User [$name] already exists.", cause)

final case class UserNotFound(
                               private val name: String,
                               private val cause: Throwable = None.orNull
                             )
  extends Exception(s"User [$name] not found.", cause)

/* ********** JSON SUPPORT ********** */
trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val userOutputJsonFormat = jsonFormat3(UserOutput)
  implicit val usersJsonFormat = jsonFormat1(UsersOutput)

  implicit val transferJsonFormat = jsonFormat3(TransferOutput)
  implicit val transfersJsonFormat = jsonFormat1(TransfersOutput)
}