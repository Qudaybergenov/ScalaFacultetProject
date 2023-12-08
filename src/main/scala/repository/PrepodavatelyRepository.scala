package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model.{Prepodavately, Akadem_stepen, Obrazovanya, Pol}

class PrepodavatelyRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val prepodCollection: MongoCollection[Document] = db.getCollection("prepodavateli")

  def getAllPrepodavateli(): Future[List[Prepodavately]] = {
    val futurePrepodavateli = prepodCollection.find().toFuture()

    futurePrepodavateli.map { docs =>
      Option(docs).map(_.map { doc =>
        Prepodavately(
          prepodId = doc.getString("prepodId"),
          fio = doc.getString("fio"),
          data_rozhdenya = doc.getString("data_rozhdenya"),
          pol = Pol.withName(doc.getString("pol")),
          akadem_stepen = Akadem_stepen.withName(doc.getString("akadem_stepen")),
          address = doc.getString("address"),
          email = doc.getString("email"),
          phone_number = doc.getString("phone_number"),
          obrazovanya = Obrazovanya.withName(doc.getString("obrazovanya")),
          specialyzacia = doc.getString("specialyzacia"),
          data_prinyatya_narbotu = doc.getString("data_prinyatya_narbotu")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getPrepodavatelById(prepodId: String): Future[Option[Prepodavately]] = {
    val prepodDocument = Document("prepodId" -> prepodId)

    prepodCollection.find(prepodDocument).headOption().map {
      case Some(doc) =>
        Some(
          Prepodavately(
            prepodId = doc.getString("prepodId"),
            fio = doc.getString("fio"),
            data_rozhdenya = doc.getString("data_rozhdenya"),
            pol = Pol.withName(doc.getString("pol")),
            akadem_stepen = Akadem_stepen.withName(doc.getString("akadem_stepen")),
            address = doc.getString("address"),
            email = doc.getString("email"),
            phone_number = doc.getString("phone_number"),
            obrazovanya = Obrazovanya.withName(doc.getString("obrazovanya")),
            specialyzacia = doc.getString("specialyzacia"),
            data_prinyatya_narbotu = doc.getString("data_prinyatya_narbotu")
          )
        )
      case None => None
    }
  }

  def addPrepodavatel(prepodavatel: Prepodavately): Future[String] = {
    val prepodDocument = Document(
      "prepodId" -> prepodavatel.prepodId,
      "fio" -> prepodavatel.fio,
      "data_rozhdenya" -> prepodavatel.data_rozhdenya,
      "pol" -> prepodavatel.pol.toString,
      "akadem_stepen" -> prepodavatel.akadem_stepen.toString,
      "address" -> prepodavatel.address,
      "email" -> prepodavatel.email,
      "phone_number" -> prepodavatel.phone_number,
      "obrazovanya" -> prepodavatel.obrazovanya.toString,
      "specialyzacia" -> prepodavatel.specialyzacia,
      "data_prinyatya_narbotu" -> prepodavatel.data_prinyatya_narbotu
    )

    prepodCollection.insertOne(prepodDocument).toFuture().map(_ => s"Преподаватель - ${prepodavatel.fio} был добавлен в базу данных.")
  }

  def deletePrepodavatel(prepodId: String): Future[String] = {
    val prepodDocument = Document("prepodId" -> prepodId)
    prepodCollection.deleteOne(prepodDocument).toFuture().map(_ => s"Преподаватель с id ${prepodId} был удален из базы данных.")
  }

  def updatePrepodavatel(prepodId: String, updatedPrepodavatel: Prepodavately): Future[String] = {
    val filter = Document("prepodId" -> prepodId)

    val prepodDocument = Document(
      "$set" -> Document(
        "prepodId" -> updatedPrepodavatel.prepodId,
        "fio" -> updatedPrepodavatel.fio,
        "data_rozhdenya" -> updatedPrepodavatel.data_rozhdenya,
        "pol" -> updatedPrepodavatel.pol.toString,
        "akadem_stepen" -> updatedPrepodavatel.akadem_stepen.toString,
        "address" -> updatedPrepodavatel.address,
        "email" -> updatedPrepodavatel.email,
        "phone_number" -> updatedPrepodavatel.phone_number,
        "obrazovanya" -> updatedPrepodavatel.obrazovanya.toString,
        "specialyzacia" -> updatedPrepodavatel.specialyzacia,
        "data_prinyatya_narbotu" -> updatedPrepodavatel.data_prinyatya_narbotu
      )
    )

    prepodCollection.updateOne(filter, prepodDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о преподавателе с id ${prepodId} была успешно обновлена."
      } else {
        s"Обновление информации о преподавателе с id ${prepodId} не выполнено. Возможно, преподаватель не найден или произошла ошибка в базе данных."
      }
    }
  }
}