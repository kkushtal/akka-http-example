package com.example

import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class UserTest extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with UserRoutes {
  override val userActor: ActorRef =
    system.actorOf(UserActor.props, "userRegistry")

  lazy val routes = userRoutes

  it should {
    "retrieve empty users" in {
      Get(s"/users") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`
        responseAs[String] shouldBe """{"users":[]}"""
      }
    }
  }

  it should {
    "create user" in {
      val user = User("Ivan", 10, "Russia")
      Post(s"/users", user) ~> routes ~> check {
        status shouldBe StatusCodes.Created
        contentType shouldBe ContentTypes.`application/json`
        responseAs[String] shouldBe s"User [${user.name}] created."
      }
    }
  }

  it should {
    "retrieve user" in {
      val name = "Ivan"
      Get(s"/users/$name") ~> routes ~> check {
        status shouldBe StatusCodes.Created
        contentType shouldBe ContentTypes.`application/json`
        responseAs[String] shouldBe """{"name":"Ivan","age":10,"countryOfResidence":"Russia"}"""
      }
    }
  }

  it should {
    "update user" in {
      val user = User("Ivan", 20, "Belarus")
      Put(s"/users/${user.name}", user) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`
        responseAs[String] shouldBe s"User [${user.name}] updated."
      }
    }
  }

  it should {
    "delete user" in {
      val name = "Ivan"
      Delete(s"/users/$name") ~> routes ~> check {
        status shouldBe StatusCodes.Created
        contentType shouldBe ContentTypes.`application/json`
        responseAs[String] shouldBe s"User [$name] deleted."
      }
    }
  }

  it should {
    "not found user" in {
      val name = "Ivan"
      Delete(s"/users/$name") ~> routes ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe ContentTypes.`application/json`
        responseAs[String] shouldBe s"User [$name] Not Found"
      }
    }
  }
}

