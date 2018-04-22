package com.example

import com.example.model._
//import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class UserTest extends WordSpec
  with Matchers with ScalaFutures with ScalatestRouteTest with UserRoutes {
  /*override val userActor: ActorRef =
    system.actorOf(UserActor.props, "userRegistry")*/

  val routes = userRoutes
  val user = User("Ivan", 20, "USA")
  val userUpd = User("Ivan", 30, "Belarus")

  "Users Web API" should {
    "retrieve empty users" in {
      Get(s"/users") ~> routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[String] shouldBe """{"users":[]}"""
      }
    }

    "create user" in {
      Post(s"/users", user) ~> routes ~> check {
        status shouldBe Created
        contentType shouldBe `text/plain(UTF-8)`
        responseAs[String] shouldBe s"User [${user.name}] created."
      }
    }

    "user already exists" in {
      Post(s"/users", user) ~> routes ~> check {
        status shouldBe Conflict
        contentType shouldBe `text/plain(UTF-8)`
        responseAs[String] shouldBe s"User [${user.name}] already exists."
      }
    }

    "retrieve user" in {
      Get(s"/users/Ivan") ~> routes ~> check {
        /* status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[String] shouldBe """{"name":"Ivan","age":10,"countryOfResidence":"Russia"}""" */
      }
    }

    "update user" in {
      Put(s"/users/${user.name}", userUpd) ~> routes ~> check {
        /*status shouldBe OK
        contentType shouldBe `text/plain(UTF-8)`
        responseAs[String] shouldBe s"User [${user.name}] updated."*/
      }
    }

    "delete user" in {
      Delete(s"/users/${user.name}") ~> routes ~> check {
        /*status shouldBe Created
        contentType shouldBe `text/plain(UTF-8)`
        responseAs[String] shouldBe s"User [${user.name}] deleted."*/
      }
    }

    "not found user" in {
      Delete(s"/users/${user.name}") ~> routes ~> check {
        /*status shouldBe NotFound
        contentType shouldBe `text/plain(UTF-8)`
        responseAs[String] shouldBe s"User [${user.name}] Not Found"*/
      }
    }

  }
  /*

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
    }*/
}

