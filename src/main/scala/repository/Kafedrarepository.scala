package repository

import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonString}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.{set, addToSet}
import org.mongodb.scala.result.UpdateResult
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import org.mongodb.scala.Document
import Model.Kafedra
import Connection._

class KafedraRepository(implicit ec: ExecutionContext) {

  def getAllKafedras(): Future[List[Kafedra]] = {
    val futureKafedras = Mongodbcollection.kafedraCollection.find().toFuture()

    futureKafedras.map { docs =>
      Option(docs).map(_.map { doc =>
        Kafedra(
          kafedraId = doc.getString("kafedraId"),
          name = doc.getString("name"),
          dekan = doc.getString("dekan"),
          spisok_prepodovatelei = Option(doc.getList("spisok_prepodovatelei", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          spisok_studentov = Option(doc.getList("spisok_studentov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          kontaktnaya_info = doc.getString("kontaktnaya_info")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getKafedraById(kafedraId: String): Future[Option[Kafedra]] = {
    val kafedraDocument = Document("kafedraId" -> kafedraId)

    Mongodbcollection.kafedraCollection.find(kafedraDocument).headOption().map {
      case Some(doc) =>
        Some(
          Kafedra(
            kafedraId = doc.getString("kafedraId"),
            name = doc.getString("name"),
            dekan = doc.getString("dekan"),
            spisok_prepodovatelei = Option(doc.getList("spisok_prepodovatelei", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            spisok_studentov = Option(doc.getList("spisok_studentov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            kontaktnaya_info = doc.getString("kontaktnaya_info")
          )
        )
      case None => None
    }
  }

  def addKafedra(kafedra: Kafedra): Future[String] = {
    val kafedraDocument = BsonDocument(
      "kafedraId" -> BsonString(kafedra.kafedraId),
      "name" -> BsonString(kafedra.name),
      "dekan" -> BsonString(kafedra.dekan),
      "spisok_prepodovatelei" -> BsonArray(kafedra.spisok_prepodovatelei.map(BsonString(_))),
      "spisok_studentov" -> BsonArray(kafedra.spisok_studentov.map(BsonString(_))),
      "kontaktnaya_info" -> BsonString(kafedra.kontaktnaya_info)
    )

    Mongodbcollection.kafedraCollection.insertOne(kafedraDocument).toFuture().map(_ => s"Кафедра - ${kafedra.name} была добавлена в базу данных.")
  }

  def deleteKafedra(kafedraId: String): Future[String] = {
    val kafedraDocument = Document("kafedraId" -> kafedraId)
    Mongodbcollection.kafedraCollection.deleteOne(kafedraDocument).toFuture().map(_ => s"Кафедра с id ${kafedraId} была удалена из базы данных.")
  }

  def updateKafedra(kafedraId: String, updatedKafedra: Kafedra): Future[String] = {
    val filter = Document("kafedraId" -> kafedraId)

    val kafedraDocument = BsonDocument(
      "$set" -> BsonDocument(
        "kafedraId" -> BsonString(updatedKafedra.kafedraId),
        "name" -> BsonString(updatedKafedra.name),
        "dekan" -> BsonString(updatedKafedra.dekan),
        "spisok_prepodovatelei" -> BsonArray(updatedKafedra.spisok_prepodovatelei.map(BsonString(_))),
        "spisok_studentov" -> BsonArray(updatedKafedra.spisok_studentov.map(BsonString(_))),
        "kontaktnaya_info" -> BsonString(updatedKafedra.kontaktnaya_info)
      )
    )

    Mongodbcollection.kafedraCollection.updateOne(filter, kafedraDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о кафедре с id ${kafedraId} была успешно обновлена."
      } else {
        s"Обновление информации о кафедре с id ${kafedraId} не выполнено. Возможно, кафедра не найдена или произошла ошибка в базе данных."
      }
    }
  }
}