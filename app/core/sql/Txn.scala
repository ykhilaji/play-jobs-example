package core.sql

import java.sql.Connection
import akka.actor.ActorSystem
import scala.util.{Failure, Success}
import scala.concurrent.Future
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import play.api.db.Database
import concurrent._

/**
  * An SQL transaction monad.
  * 1) Encapsulates and composes transaction statements to be sent to the database.
  * 2) Enforces the use of a dedicated ExecutionContext.
  */
case class Txn[+A](private val atomic: Connection => A) extends core.Logger {

  def map[B](f: A => B): Txn[B] =
    Txn(f compose atomic)

  def flatMap[B](f: A => Txn[B]): Txn[B] =
    Txn(connection => f(atomic(connection)).atomic(connection))

  def zip[B](tx: Txn[B]): Txn[(A, B)] = flatMap { a =>
    tx.map { b =>
      (a, b)
    }
  }

  def commit(logException: Boolean = true)(implicit db: Database,
                                           system: ActorSystem): Future[A] = {
    implicit val ec = system.dispatchers.lookup("txn.execution-context")

    val result = Future {
      db.withConnection(atomic)
    }

    if (logException) {
      result.onComplete {
        case Failure(ex) => LOG.error("Query failed", ex)
        case _           =>
      }
    }

    result
  }

  def commitTransaction(logException: Boolean = true)(
      implicit db: Database,
      system: ActorSystem): Future[A] = {
    implicit val ec = system.dispatchers.lookup("txn.execution-context")

    val result = Future {
      db.withTransaction(atomic)
    }

    if (logException) {
      result.onComplete {
        case Failure(ex) => LOG.error("Query failed", ex)
        case _           =>
      }
    }
    result
  }

  def commitException(implicit db: Database,
                      system: ActorSystem): Future[Either[String, A]] = {
    implicit val ec = system.dispatchers.lookup("txn.execution-context")
    val result = Future { db.withConnection(atomic) }
    val promise = Promise[Either[String, A]]()
    result.onComplete {
      case Success(s)  => promise success Right(s)
      case Failure(ex) => promise success Left(ex.getMessage)
    }
    promise.future
  }

  def commitTransactionException(
      implicit db: Database,
      system: ActorSystem): Future[Either[String, A]] = {
    implicit val ec = system.dispatchers.lookup("txn.execution-context")
    val result = Future { db.withTransaction(atomic) }
    val promise = Promise[Either[String, A]]()
    result.onComplete {
      case Success(s)  => promise success Right(s)
      case Failure(ex) => promise success Left(ex.getMessage)
    }
    promise.future
  }

}

object Txn {

  def pure[A](a: => A) = Txn(_ => a)

  def seq[A](maybeTx: Option[Txn[A]]): Txn[Option[A]] =
    maybeTx match {
      case Some(tx) => tx.map(a => Some(a))
      case None     => pure(None)
    }

  def seq[A, F[X] <: TraversableOnce[X]](tas: F[Txn[A]])(
      implicit cbf: CanBuildFrom[F[Txn[A]], A, F[A]]): Txn[F[A]] = {
    tas.foldLeft(pure(cbf(tas))) { (tr, ta) =>
      for {
        r <- tr
        a <- ta
      } yield r += a
    } map (_.result())
  }

  def failure[T](ex: Throwable) = pure[T](throw ex)
}
