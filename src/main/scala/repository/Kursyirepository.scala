package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.{set, combine, addToSet}
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.model.Sorts.descending
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model.{Kursyi, KursStatus}

class KursyiRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val kursyiCollection: MongoCollection[Document] = db.getCollection("kursyi")

  def getAllKursyi(): Future[List[Kursyi]] = {
    val futureKursyi = kursyiCollection.find().toFuture()

    futureKursyi.map { docs =>
      Option(docs).map(_.map { doc =>
        Kursyi(
          kursId = doc.getString("kursId"),
          name = doc.getString("name"),
          opicanyia = doc.getString("opicanyia"),
          prepodavatel = Option(doc.getList("prepodavatel", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          spisok_studentov = Option(doc.getList("spisok_studentov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          mesto_provedenya = doc.getString("mesto_provedenya"),
          vremyia_provedenya = doc.getString("vremyia_provedenya"),
          status = KursStatus.withName(doc.getString("status")),
          kontaktnayia_info = doc.getString("kontaktnayia_info")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getKursyiById(kursId: String): Future[Option[Kursyi]] = {
    val kursyiDocument = Document("kursId" -> kursId)

    kursyiCollection.find(kursyiDocument).headOption().map {
      case Some(doc) =>
        Some(
          Kursyi(
            kursId = doc.getString("kursId"),
            name = doc.getString("name"),
            opicanyia = doc.getString("opicanyia"),
            prepodavatel = Option(doc.getList("prepodavatel", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            spisok_studentov = Option(doc.getList("spisok_studentov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            mesto_provedenya = doc.getString("mesto_provedenya"),
            vremyia_provedenya = doc.getString("vremyia_provedenya"),
            status = KursStatus.withName(doc.getString("status")),
            kontaktnayia_info = doc.getString("kontaktnayia_info")
          )
        )
      case None => None
    }
  }

  def addKursyi(kursyi: Kursyi): Future[String] = {
    val kursyiDocument = Document(
      "kursId" -> kursyi.kursId,
      "name" -> kursyi.name,
      "opicanyia" -> kursyi.opicanyia,
      "prepodavatel" -> kursyi.prepodavatel,
      "spisok_studentov" -> kursyi.spisok_studentov,
      "mesto_provedenya" -> kursyi.mesto_provedenya,
      "vremyia_provedenya" -> kursyi.vremyia_provedenya,
      "status" -> kursyi.status.toString,
      "kontaktnayia_info" -> kursyi.kontaktnayia_info
    )

    kursyiCollection.insertOne(kursyiDocument).toFuture().map(_ => s"Курс - ${kursyi.name} был добавлен в базу данных.")
  }

  def deleteKursyi(kursId: String): Future[String] = {
    val kursyiDocument = Document("kursId" -> kursId)
    kursyiCollection.deleteOne(kursyiDocument).toFuture().map(_ => s"Курс с id ${kursId} был удален из базы данных.")
  }

  def updateKursyi(kursId: String, updatedKursyi: Kursyi): Future[String] = {
    val filter = Document("kursId" -> kursId)

    val kursyiDocument = Document(
      "$set" -> Document(
        "kursId" -> updatedKursyi.kursId,
        "name" -> updatedKursyi.name,
        "opicanyia" -> updatedKursyi.opicanyia,
        "prepodavatel" -> updatedKursyi.prepodavatel,
        "spisok_studentov" -> updatedKursyi.spisok_studentov,
        "mesto_provedenya" -> updatedKursyi.mesto_provedenya,
        "vremyia_provedenya" -> updatedKursyi.vremyia_provedenya,
        "status" -> updatedKursyi.status.toString,
        "kontaktnayia_info" -> updatedKursyi.kontaktnayia_info
      )
    )

    kursyiCollection.updateOne(filter, kursyiDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о курсе с id ${kursId} была успешно обновлена."
      } else {
        s"Обновление информации о курсе с id ${kursId} не выполнено. Возможно, курс не найден или произошла ошибка в базе данных."
      }
    }
  }
}