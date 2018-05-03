package com.example.Database

import com.example.model._
import scalikejdbc._

object TransferOutput extends SQLSyntaxSupport[TransferOutput] {
  override val tableName = "transfers"
  private val t = Transfer.syntax("t")
  private val from_user = User.syntax("from_user")
  private val to_user = User.syntax("to_user")

  def apply(t: SyntaxProvider[TransferOutput])(rs: WrappedResultSet): TransferOutput = {
    apply(t.resultName)(rs)
  }

  def apply(t: ResultName[TransferOutput])(rs: WrappedResultSet): TransferOutput = new TransferOutput(
    amount = rs.double(
      t.amount),
    from = rs.string(t.from),
    to = rs.string(t.to)
  )


  def apply(t: SyntaxProvider[Transfer],
            fromUser: SyntaxProvider[User],
            toUser: SyntaxProvider[User])(rs: WrappedResultSet): TransferOutput = {
    apply(t.resultName, fromUser.resultName, toUser.resultName)(rs)
  }

  def apply(t: ResultName[Transfer],
            fromUser: ResultName[User],
            toUser: ResultName[User])(rs: WrappedResultSet): TransferOutput = new TransferOutput(
    amount = rs.double(t.amount),
    from = rs.string(fromUser.name),
    to = rs.string(toUser.name)
  )

  def selectFull(where: SQLSyntax = SQLSyntax.empty)(implicit session: DBSession = AutoSession): List[TransferOutput] = {
    sql"""select
            ${t.result.amount},
            ${from_user.result.name},
            ${to_user.result.name}
          from
            ${Transfer as t}
            left join ${User as from_user}
              on ${t.fromUserId} = ${from_user.id}
            left join ${User as to_user}
              on ${t.toUserId} = ${to_user.id}
          $where"""
      .map(TransferOutput(t, from_user, to_user)).list.apply()
  }

  def selectAll()(implicit session: DBSession = AutoSession): List[TransferOutput] = {
    val where =
      sqls"""where
               ${t.fromUserId} = ${from_user.id}
               and
               ${t.toUserId} = ${to_user.id}"""
    selectFull(where)
  }

  def toUserSelect(userId: Long)(implicit session: DBSession = AutoSession): List[TransferOutput] = {
    val where = sqls"""where ${t.toUserId} = $userId"""
    selectFull(where)
  }

  def fromUserSelect(userId: Long)(implicit session: DBSession = AutoSession): List[TransferOutput] = {
    val where = sqls"""where ${t.fromUserId} = $userId"""
    selectFull(where)
  }


}

object Transfer extends SQLSyntaxSupport[Transfer] {
  override val tableName = "transfers"
  private val t = syntax("t")

  def apply(t: SyntaxProvider[Transfer])(rs: WrappedResultSet): Transfer = {
    apply(t.resultName)(rs)
  }

  def apply(t: ResultName[Transfer])(rs: WrappedResultSet): Transfer = new Transfer(
    id = rs.int(t.id),
    amount = rs.double(t.amount),
    fromUserId = rs.int(t.fromUserId),
    toUserId = rs.int(t.toUserId)
    /*createdAt = rs.zonedDateTime(t.createdAt)*/
  )

  def insert(amount: Double, fromUserId: Long, toUserId: Long)(implicit session: DBSession = AutoSession): Long = {
    sql"""insert into $table
          (${column.amount}, ${column.fromUserId}, ${column.toUserId})
          values ($amount, $fromUserId, $toUserId)"""
      .updateAndReturnGeneratedKey.apply()
  }

  def selectAll(where: SQLSyntax = SQLSyntax.empty)(implicit session: DBSession = AutoSession): List[Transfer] = {
    sql"""select ${t.result.*}
          from ${Transfer as t}
          $where
          order by ${t.id}"""
      .map(Transfer(t)).list.apply()
  }

  def toUserSelectAll(userId: Long)(implicit session: DBSession = AutoSession): List[Transfer] = {
    val where = sqls"""where ${t.toUserId} = $userId"""
    selectAll(where)
  }

  def fromUserSelectAll(userId: Long)(implicit session: DBSession = AutoSession): List[Transfer] = {
    val where = sqls"""where ${t.fromUserId} = $userId"""
    selectAll(where)
  }

}
