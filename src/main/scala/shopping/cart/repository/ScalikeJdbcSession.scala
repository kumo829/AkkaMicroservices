package shopping.cart.repository

import akka.japi.function
import akka.projection.jdbc.JdbcSession
import scalikejdbc.DB

import java.sql.Connection

object ScalikeJdbcSession {
  def withSession[R](f: ScalikeJdbcSession => R): R = {
    val session = new ScalikeJdbcSession()
    try {
      f(session)
    } finally {
      session.close()
    }
  }
}


final class ScalikeJdbcSession extends JdbcSession {
  val db: DB = DB.connect()
  db.autoClose(false)

  override def withConnection[Result](func: function.Function[Connection, Result]): Result = {
    db.begin()
    db.withinTxWithConnection(func(_))
  }

  override def commit(): Unit = db.commit()

  override def rollback(): Unit = db.rollback()

  override def close(): Unit = db.close()
}