package com.example

import com.example.model._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

object DataBase {
  private implicit val session: DBSession = AutoSession

  private def getUserId(name: String): Long = Database.User.select(name) match {
    case Some(u: User) => u.id
    case _ => throw UserNotFound(name)
  }

  /* ********** USER ********** */
  def addUser(user: UserOutput): Future[Unit] = DB futureLocalTx { implicit session =>
    Future {
      val userId = Database.User.insert(user)
      Database.Balance.insert(userId, user.balance)
    }
  }

  def getUser(name: String): Future[UserOutput] = DB futureLocalTx { implicit session =>
    Future {
      Database.UserOutput.select(name).get
    }
  }

  def getUsers: Future[UsersOutput] = DB futureLocalTx { implicit session =>
    Future {
      val users = Database.UserOutput.selectAll()
      UsersOutput(users)
    }
  }

  def editUser(name: String, user: UserOutput): Future[Unit] = DB futureLocalTx { implicit session =>
    Future {
      val userId = Database.User.update(name, user)
      /*count match {
        case Some(c: Int) if c > 0 => Future.unit
        case Some(c: Int) if c == 0 => throw UserNotFound(name)
        case _ => throw UserAlreadyExists(name)
      }*/
    }
  }

  def removeUser(name: String): Future[Unit] = DB futureLocalTx { implicit session =>
    Future {
      val userId = Database.User.delete(name)
      /*count match {
      case c: Int if c > 0 => Future.unit
      case c: Int if c == 0 => throw UserNotFound(name)
      }*/
    }
  }

  /* ********** BALANCE ********** */
  def editBalance(name: String, amount: Double): Future[Unit] = DB futureLocalTx { implicit session =>
    Future {
      val userId = getUserId(name)
      Database.Balance.update(userId, amount)
    }
  }

  /* ********** TRANSFER ********** */
  def makeTransfer(transfer: TransferOutput): Future[Unit] = DB futureLocalTx { implicit session =>
    Future {
      val fromUserId = getUserId(transfer.from)
      val toUserId = getUserId(transfer.to)

      Database.Balance.update(fromUserId, -1 * transfer.amount)
      Database.Balance.update(toUserId, transfer.amount)

      Database.Transfer.insert(transfer.amount, fromUserId, toUserId)
    }
  }

  def getTransfers: Future[TransfersOutput] = DB futureLocalTx { implicit session =>
    Future {
      val transfers = Database.TransferOutput.selectAll()
      TransfersOutput(transfers)
    }
  }

  def getTransfers(name: String): Future[TransfersOutput] = DB futureLocalTx { implicit session =>
    Future {
      val userId = getUserId(name)
      val transfers = Database.TransferOutput.fromUserSelect(userId)
      TransfersOutput(transfers)
    }
  }

  /*def getTransfersToUser(name: String): Future[TransfersOutput] = DB futureLocalTx { implicit session =>
    Future {
      val userId = getUserId(name)
      val transfers = Transfer.toUserSelectAll(userId)
      TransfersOutput(transfers)
    }
  }

  def getTransfersFromUser(name: String): Future[TransfersOutput] = DB futureLocalTx { implicit session =>
    Future {
      val userId = getUserId(name)
      val transfers = Transfer.fromUserSelectAll(userId)
      TransfersOutput(transfers)
    }
  }*/



}
