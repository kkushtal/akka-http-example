package com.example.Database

import com.example.model._
import scalikejdbc._

object Balance extends SQLSyntaxSupport[Balance] {
  override val tableName = "balances"

  def apply(t: SyntaxProvider[Balance])(rs: WrappedResultSet): Balance = {
    apply(t.resultName)(rs)
  }

  def apply(t: ResultName[Balance])(rs: WrappedResultSet): Balance = new Balance(
    userId = rs.int(t.userId),
    amount = rs.double(t.amount)
  )

  def insert(userId: Long, amount: Double)(implicit session: DBSession = AutoSession): Long = {
    sql"""insert into $table
          (${column.userId}, ${column.amount})
          values ($userId, $amount)"""
      .updateAndReturnGeneratedKey.apply()
  }

  def update(userId: Long, amount: Double)(implicit session: DBSession = AutoSession): Long = {
    sql"""update $table
          set ${column.amount} = ${column.amount} + $amount
          where ${column.userId} = $userId"""
      .update.apply()
  }

}
