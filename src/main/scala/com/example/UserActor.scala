package com.example

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe

import scala.concurrent.ExecutionContext

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])


object UserActor {

  final case object GetUsers

  final case class CreateUser(user: User)

  final case class GetUser(name: String)

  final case class UpdateUser(name: String)

  final case class DeleteUser(name: String)

  final case class UserCreated(message: String)

  final case class UserUpdated(message: String)

  final case class UserDeleted(message: String)

  final case class UserNotFound(message: String)

  final case class UserAlreadyExists(message: String)

  def props: Props = Props[UserActor]
}

class UserActor extends Actor with ActorLogging {

  import UserActor._

  implicit val ec: ExecutionContext = context.dispatcher

  def userNotFound(name: String): UserNotFound = UserNotFound(s"User [$name] Not Found")

  def receive: Receive = {
    case GetUsers =>
      UserRepository.findAll pipeTo sender

    case CreateUser(user) =>
      UserRepository.insert(user) map {
        case true => UserCreated(s"User [${user.name}] created.")
        case _ => UserAlreadyExists(s"User [${user.name}] Already Exists")
      } pipeTo sender

    case GetUser(name) =>
      UserRepository.find(name) map {
        case Some(user) => user
        case _ => userNotFound(name)
      } pipeTo sender

    case DeleteUser(name) =>
      UserRepository.delete(name) map {
        case Some(user) => UserDeleted(s"User [${user.name}] deleted.")
        case _ => userNotFound(name)
      } pipeTo sender

    case UpdateUser(name) =>
      UserRepository.update(name) map {
        case Some(user) => UserUpdated(s"User [${user.name}] updated.")
        case _ => userNotFound(name)
      } pipeTo sender
  }
}