package com.example.Database

import com.example.model._
import scalikejdbc._

object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"
  private val u = User.syntax("u")

  def apply(u: SyntaxProvider[User])(rs: WrappedResultSet): User = {
    apply(u.resultName)(rs)
  }

  def apply(u: ResultName[User])(rs: WrappedResultSet): User = new User(
    id = rs.long(u.id),
    name = rs.string(u.name),
    age = rs.int(u.age)
  )

  def insert(user: UserOutput)(implicit session: DBSession = AutoSession): Long = {
    sql"""insert into $table
          (${column.name}, ${column.age})
          values (${user.name}, ${user.age})"""
      .updateAndReturnGeneratedKey.apply()
  }

  def select(name: String)(implicit session: DBSession = AutoSession): Option[User] = {
    sql"""select ${u.result.*}
          from ${User as u}
          where ${u.name} = ${name}
          order by ${u.id}"""
      .map(User(u)).single.apply()
  }

  def selectAll()(implicit session: DBSession = AutoSession): List[User] = {
    sql"""select ${u.result.*}
          from ${User as u}
          order by ${u.id}"""
      .map(User(u)).list.apply()
  }

  def update(name: String, newUser: UserOutput)(implicit session: DBSession = AutoSession): Long = {
    sql"""update $table
          set ${column.name} = ${newUser.name}
          where  ${column.name} = $name"""
      .update.apply()
  }

  def delete(name: String)(implicit session: DBSession = AutoSession): Long = {
    sql"""delete from $table
          where ${column.name} = $name"""
      .update.apply()
  }
}


object UserOutput extends SQLSyntaxSupport[UserOutput] {
  override val tableName = "users"
  private val u = User.syntax("u")
  private val b = Balance.syntax("b")

  def apply(u: SyntaxProvider[User], b: SyntaxProvider[Balance])(rs: WrappedResultSet): UserOutput = {
    apply(u.resultName, b.resultName)(rs)
  }

  def apply(u: ResultName[User], b: ResultName[Balance])(rs: WrappedResultSet): UserOutput = new UserOutput(
    name = rs.string(u.name),
    age = rs.int(u.age),
    balance = rs.double(b.amount)
  )

  def selectAll()(implicit session: DBSession = AutoSession): List[UserOutput] = {
    sql"""select ${u.result.*}, ${b.result.*}
          from ${User as u}
          left join ${Balance as b}
          on ${u.id} = ${b.userId}"""
      .map(UserOutput(u, b)).list.apply()
  }

  def select(name: String)(implicit session: DBSession = AutoSession): Option[UserOutput] = {
    sql"""select ${u.result.*}, ${b.result.*}
          from ${User as u} left join ${Balance as b}
          on ${u.id} = ${b.userId}
          where ${u.name} = ${name}"""
      .map(UserOutput(u, b)).single.apply()
  }
}