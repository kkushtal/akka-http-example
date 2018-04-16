package com.example

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

object QuickstartServer extends App with UserRoutes {

  implicit val system: ActorSystem = ActorSystem("akkaHttpExample")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val userActor: ActorRef = system.actorOf(UserActor.props, "userActor")
  lazy val routes: Route = userRoutes

  Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")
  Await.result(system.whenTerminated, Duration.Inf)
}

