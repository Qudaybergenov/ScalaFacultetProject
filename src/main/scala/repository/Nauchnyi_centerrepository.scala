package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model.Nauchnyi_center

class NauchnyiCenterRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val centerCollection: MongoCollection[Document] = db.getCollection("nauchnye_centry")

  def getAllCenters(): Future[List[Nauchnyi_center]] = {
    val futureCenters = centerCollection.find().toFuture()

    futureCenters.map { docs =>
      Option(docs).map(_.map { doc =>
        Nauchnyi_center(
          centerId = doc.getString("centerId"),
          name = doc.getString("name"),
          director = doc.getString("director"),
          opicanya = doc.getString("opicanya"),
          spisok_proektov = Option(doc.getList("spisok_proektov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          spisok_studentov = Option(doc.getList("spisok_studentov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          kontaktnayia_info = doc.getString("kontaktnayia_info")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getCenterById(centerId: String): Future[Option[Nauchnyi_center]] = {
    val centerDocument = Document("centerId" -> centerId)

    centerCollection.find(centerDocument).headOption().map {
      case Some(doc) =>
        Some(
          Nauchnyi_center(
            centerId = doc.getString("centerId"),
            name = doc.getString("name"),
            director = doc.getString("director"),
            opicanya = doc.getString("opicanya"),
            spisok_proektov = Option(doc.getList("spisok_proektov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            spisok_studentov = Option(doc.getList("spisok_studentov", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            kontaktnayia_info = doc.getString("kontaktnayia_info")
          )
        )
      case None => None
    }
  }

  def addCenter(center: Nauchnyi_center): Future[String] = {
    val centerDocument = Document(
      "centerId" -> center.centerId,
      "name" -> center.name,
      "director" -> center.director,
      "opicanya" -> center.opicanya,
      "spisok_proektov" -> center.spisok_proektov,
      "spisok_studentov" -> center.spisok_studentov,
      "kontaktnayia_info" -> center.kontaktnayia_info
    )

    centerCollection.insertOne(centerDocument).toFuture().map(_ => s"Научный центр - ${center.name} был добавлен в базу данных.")
  }

  def deleteCenter(centerId: String): Future[String] = {
    val centerDocument = Document("centerId" -> centerId)
    centerCollection.deleteOne(centerDocument).toFuture().map(_ => s"Научный центр с id ${centerId} был удален из базы данных.")
  }

  def updateCenter(centerId: String, updatedCenter: Nauchnyi_center): Future[String] = {
    val filter = Document("centerId" -> centerId)

    val centerDocument = Document(
      "$set" -> Document(
        "centerId" -> updatedCenter.centerId,
        "name" -> updatedCenter.name,
        "director" -> updatedCenter.director,
        "opicanya" -> updatedCenter.opicanya,
        "spisok_proektov" -> updatedCenter.spisok_proektov,
        "spisok_studentov" -> updatedCenter.spisok_studentov,
        "kontaktnayia_info" -> updatedCenter.kontaktnayia_info
      )
    )

    centerCollection.updateOne(filter, centerDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о научном центре с id ${centerId} была успешно обновлена."
      } else {
        s"Обновление информации о научном центре с id ${centerId} не выполнено. Возможно, центр не найден или произошла ошибка в базе данных."
      }
    }
  }
}