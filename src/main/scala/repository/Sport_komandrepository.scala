package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model.{Sport_komand, Vid_sporta}

class SportKomandRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val sportKomandCollection: MongoCollection[Document] = db.getCollection("sport_komands")

  def getAllSportKomands(): Future[List[Sport_komand]] = {
    val futureSportKomands = sportKomandCollection.find().toFuture()

    futureSportKomands.map { docs =>
      Option(docs).map(_.map { doc =>
        Sport_komand(
          komandID = doc.getString("komandID"),
          name = doc.getString("name"),
          vid_sporta = Vid_sporta.withName(doc.getString("vid_sporta")),
          trener = doc.getString("trener"),
          sostav = Option(doc.getList("sostav", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          dostyzhenyia = doc.getString("dostyzhenyia"),
          populyiarnost = doc.getString("populyiarnost"),
          raspisanyia = doc.getString("raspisanyia")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getSportKomandById(komandID: String): Future[Option[Sport_komand]] = {
    val sportKomandDocument = Document("komandID" -> komandID)

    sportKomandCollection.find(sportKomandDocument).headOption().map {
      case Some(doc) =>
        Some(
          Sport_komand(
            komandID = doc.getString("komandID"),
            name = doc.getString("name"),
            vid_sporta = Vid_sporta.withName(doc.getString("vid_sporta")),
            trener = doc.getString("trener"),
            sostav = Option(doc.getList("sostav", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            dostyzhenyia = doc.getString("dostyzhenyia"),
            populyiarnost = doc.getString("populyiarnost"),
            raspisanyia = doc.getString("raspisanyia")
          )
        )
      case None => None
    }
  }

  def addSportKomand(sportKomand: Sport_komand): Future[String] = {
    val sportKomandDocument = Document(
      "komandID" -> sportKomand.komandID,
      "name" -> sportKomand.name,
      "vid_sporta" -> sportKomand.vid_sporta.toString,
      "trener" -> sportKomand.trener,
      "sostav" -> sportKomand.sostav,
      "dostyzhenyia" -> sportKomand.dostyzhenyia,
      "populyiarnost" -> sportKomand.populyiarnost,
      "raspisanyia" -> sportKomand.raspisanyia
    )

    sportKomandCollection.insertOne(sportKomandDocument).toFuture().map(_ => s"Спортивная команда - ${sportKomand.name} была добавлена в базу данных.")
  }

  def deleteSportKomand(komandID: String): Future[String] = {
    val sportKomandDocument = Document("komandID" -> komandID)
    sportKomandCollection.deleteOne(sportKomandDocument).toFuture().map(_ => s"Спортивная команда с id ${komandID} была удалена из базы данных.")
  }

  def updateSportKomand(komandID: String, updatedSportKomand: Sport_komand): Future[String] = {
    val filter = Document("komandID" -> komandID)

    val sportKomandDocument = Document(
      "$set" -> Document(
        "komandID" -> updatedSportKomand.komandID,
        "name" -> updatedSportKomand.name,
        "vid_sporta" -> updatedSportKomand.vid_sporta.toString,
        "trener" -> updatedSportKomand.trener,
        "sostav" -> updatedSportKomand.sostav,
        "dostyzhenyia" -> updatedSportKomand.dostyzhenyia,
        "populyiarnost" -> updatedSportKomand.populyiarnost,
        "raspisanyia" -> updatedSportKomand.raspisanyia
      )
    )

    sportKomandCollection.updateOne(filter, sportKomandDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о спортивной команде с id ${komandID} была успешно обновлена."
      } else {
        s"Обновление информации о спортивной команде с id ${komandID} не выполнено. Возможно, команда не найдена или произошла ошибка в базе данных."
      }
    }
  }
}