package com.example

import com.example.model._
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object UserRepository {

  private var users = Map.empty[String, User]

  private def find(name: String): User = {
    users.get(name) match {
      case Some(user) => user
      case _ => throw UserNotFound(name)
    }
  }

  def selectAll: Future[Users] = Future(Users(users.values.toSeq))

  def select(name: String): Future[User] = Future(find(name))

  def insert(user: User): Future[Unit] = Future {
    val name = user.name
    if (users.contains(name))
      throw UserAlreadyExists(name)
    users += (name -> user)
    Future.unit
  }

  def delete(name: String): Future[User] = Future {
    val user = find(name)
    users -= name
    user
  }

  def update(name: String, newUser: User): Future[User] = Future {
    val oldUser = find(name)
    users += (name -> newUser)
    oldUser
  }
}