package com.example

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import scalikejdbc.config.DBs

object QuickstartServer extends App with Routes {

  DBs.setupAll()

  implicit val system: ActorSystem = ActorSystem("akkaHttpExample")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val routes: Route = userRoutes

  Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")
  Await.result(system.whenTerminated, Duration.Inf)
}

