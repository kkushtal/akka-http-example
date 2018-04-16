package com.example

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object UserRepository {

  var users = Set.empty[User]

  private def findBy(name: String): Option[User] = users.find(name == _.name)

  private def notExists(user: User): Boolean = !users.exists(user.name == _.name)

  def find(name: String): Future[Option[User]] = Future(findBy(name))

  def findAll: Future[Users] = Future(Users(users.toSeq))

  def insert(user: User): Future[Boolean] = Future {
    if (notExists(user)) {
      users += user
      true
    } else false
  }

  def delete(name: String): Future[Option[User]] = Future {
    val user = findBy(name)
    if (user.isDefined) users -= user.get
    user
  }

  def update(name: String): Future[Option[User]] = Future {
    val user = findBy(name)
    if (user.isDefined) users += user.get
    user
  }
}