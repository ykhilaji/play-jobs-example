package repository

import anorm._
import anorm.SqlParser._
import play.api.libs.json.JsValue
import core.sql.JsValue._
import core.sql.Txn
import anorm.JodaParameterMetaData._
import anorm.SqlParser.get
import javax.inject.Singleton
import org.joda.time.DateTime

case class BordereauModel(id: Long, colisIds: Seq[String])

object BordereauModel {
  val table = "bordereaux"
  val oldDays = 1

  private val parserBordereau: RowParser[BordereauModel] =
    for {
      id <- get[Long]("id")
      colisIds <- get[JsValue]("data")
    } yield BordereauModel(id = id, colisIds = colisIds.as[Seq[String]])

  private val parserIds: RowParser[Long] = get[Long]("id")

  trait BordereauRepository {

    def insert(userId: String, data: JsValue): Txn[Boolean]
    def findFromTo(userId: String,
                   fromDate: DateTime,
                   toDate: DateTime): Txn[Seq[BordereauModel]]
    def purgeOldHistorique: Txn[Int]
    def countOldHistorique: Txn[Int]
    def purgeOld: Txn[Int]
    def purgeOldIds(oldIds: Seq[Long]): Txn[Int]

  }

  @Singleton
  class BordereauRepositoryImpl extends BordereauRepository {
    val table = "bordereaux"

    def insert(userId: String, data: JsValue): Txn[Boolean] =
      Txn { implicit c =>
        SQL"""
        INSERT INTO #$table(user_id, data)
        VALUES($userId, $data)
      """.execute()
      }

    def findFromTo(userId: String,
                   fromDate: DateTime,
                   toDate: DateTime): Txn[Seq[BordereauModel]] = Txn {
      implicit c =>
        val statement =
          SQL"SELECT id, data FROM #$table WHERE user_id = $userId AND created >= $fromDate AND created < $toDate ORDER BY created ASC"
        statement.as(parserBordereau.*)
    }

    def purgeOldHistorique: Txn[Int] = Txn { implicit c =>
      SQL"DELETE FROM #$table WHERE created < ${DateTime.now.minusDays(oldDays).withTimeAtStartOfDay}"
        .executeUpdate()
    }

    def countOldHistorique: Txn[Int] = Txn { implicit c =>
      SQL"SELECT COUNT(*) FROM #$table   WHERE created < ${DateTime.now.minusDays(oldDays).withTimeAtStartOfDay}"
        .as(scalar[Int].single)
    }

    def purgeOld: Txn[Int] = Txn { implicit c =>
      SQL"""
        DELETE
        FROM
          #$table
        WHERE 
          created < ${DateTime.now.minusDays(oldDays).withTimeAtStartOfDay}
      """.executeUpdate()
    }

    def purgeOldIds(oldIds: Seq[Long]): Txn[Int] = Txn { implicit c =>
      SQL"""
        DELETE
        FROM
          #$table
        WHERE
        id IN ($oldIds)
      """.executeUpdate()
    }
  }
}
