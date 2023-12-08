package repository

import java.util.Date
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}
import org.mongodb.scala.model.Filters.{equal, regex}
import org.mongodb.scala.model.Updates.{set, combine, addToSet}
import org.mongodb.scala.result.UpdateResult
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Connection._
import Model._

class FacultetRepository(implicit ec: ExecutionContext){

  def getAllFacultets(): Future[List[Facultet]] = {
    val futureFacultets = Mongodbcollection.facultetCollection.find().toFuture()

    futureFacultets.map { docs =>
      Option(docs).map(_.map { doc =>
        Facultet(
          facultetId = doc.getString("facultetId"),
          name = doc.getString("name"),
          spisok_kafedr = Option(doc.getList("spisok_kafedr", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          spisok_studentof = Option(doc.getList("spisok_studentof", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          raspolozhenya = doc.getString("raspolozhenya"),
          god_osnovanya = doc.getString("god_osnovanya"),
          kontaktnyi_tel = doc.getString("kontaktnyi_tel"),
          email = doc.getString("email"),
          spisok_sportivnyx_komand = Option(doc.getList("spisok_sportivnyx_komand", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          spisok_nauchnyx_center = Option(doc.getList("spisok_nauchnyx_center", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          spisok_kursov = Option(doc.getList("spisok_kursov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getFacultetById(facultetId: String): Future[Option[Facultet]] = {
    val facultetDocument = Document("facultetId" -> facultetId)

    Mongodbcollection.facultetCollection.find(facultetDocument).headOption().map {
      case Some(doc) =>
        Some(
          Facultet(
            facultetId = doc.getString("facultetId"),
            name = doc.getString("name"),
            spisok_kafedr = Option(doc.getList("spisok_kafedr", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            spisok_studentof = Option(doc.getList("spisok_studentof", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            raspolozhenya = doc.getString("raspolozhenya"),
            god_osnovanya = doc.getString("god_osnovanya"),
            kontaktnyi_tel = doc.getString("kontaktnyi_tel"),
            email = doc.getString("email"),
            spisok_sportivnyx_komand = Option(doc.getList("spisok_sportivnyx_komand", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            spisok_nauchnyx_center = Option(doc.getList("spisok_nauchnyx_center", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            spisok_kursov = Option(doc.getList("spisok_kursov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)
          )
        )
      case None => None
    }
  }

  def addFacultet(facultet: Facultet): Future[String] = {
    val facultetDocument = BsonDocument(
      "facultetId" -> BsonString(facultet.facultetId),
      "name" -> BsonString(facultet.name),
      "spisok_kafedr" -> BsonArray(facultet.spisok_kafedr.map(BsonString(_))),
      "spisok_studentof" -> BsonArray(facultet.spisok_studentof.map(BsonString(_))),
      "raspolozhenya" -> BsonString(facultet.raspolozhenya),
      "god_osnovanya" -> BsonString(facultet.god_osnovanya),
      "kontaktnyi_tel" -> BsonString(facultet.kontaktnyi_tel),
      "email" -> BsonString(facultet.email),
      "spisok_sportivnyx_komand" -> BsonArray(facultet.spisok_sportivnyx_komand.map(BsonString(_))),
      "spisok_nauchnyx_center" -> BsonArray(facultet.spisok_nauchnyx_center.map(BsonString(_))),
      "spisok_kursov" -> BsonArray(facultet.spisok_kursov.map(BsonString(_)))
    )

    Mongodbcollection.facultetCollection.insertOne(facultetDocument).toFuture().map(_ => s"Факультет - ${facultet.name} был добавлен в базу данных.")
  }

  def deleteFacultet(facultetId: String): Future[String] = {
    val facultetDocument = Document("facultetId" -> facultetId)
    Mongodbcollection.facultetCollection.deleteOne(facultetDocument).toFuture().map(_ => s"Факультет с id ${facultetId} был удален из базы данных.")
  }

  def updateFacultet(facultetId: String, updatedFacultet: Facultet): Future[String] = {
    val filter = Document("facultetId" -> facultetId)

    val facultetDocument = BsonDocument(
      "$set" -> BsonDocument(
        "facultetId" -> BsonString(updatedFacultet.facultetId),
        "name" -> BsonString(updatedFacultet.name),
        "spisok_kafedr" -> BsonArray(updatedFacultet.spisok_kafedr.map(BsonString(_))),
        "spisok_studentof" -> BsonArray(updatedFacultet.spisok_studentof.map(BsonString(_))),
        "raspolozhenya" -> BsonString(updatedFacultet.raspolozhenya),
        "god_osnovanya" -> BsonString(updatedFacultet.god_osnovanya),
        "kontaktnyi_tel" -> BsonString(updatedFacultet.kontaktnyi_tel),
        "email" -> BsonString(updatedFacultet.email),
        "spisok_sportivnyx_komand" -> BsonArray(updatedFacultet.spisok_sportivnyx_komand.map(BsonString(_))),
        "spisok_nauchnyx_center" -> BsonArray(updatedFacultet.spisok_nauchnyx_center.map(BsonString(_))),
        "spisok_kursov" -> BsonArray(updatedFacultet.spisok_kursov.map(BsonString(_)))
      )
    )

    Mongodbcollection.facultetCollection.updateOne(filter, facultetDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о факультете с id ${facultetId} была успешно обновлена."
      } else {
        s"Обновление информации о факультете с id ${facultetId} не выполнено. Возможно, факультет не найден или произошла ошибка в базе данных."
      }

    }
  }
}