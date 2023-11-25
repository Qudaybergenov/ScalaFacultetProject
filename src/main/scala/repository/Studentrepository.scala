package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.model.Sorts.descending
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Updates.{combine, set}
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model.{Kurs, Status, Uroven_angl, Pol}
import Model._

class StudentRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val studentCollection: MongoCollection[Document] = db.getCollection("students")

  def getAllStudents(): Future[List[Student]] = {
    val futureStudents = studentCollection.find().toFuture()

    futureStudents.map { docs =>
      Option(docs).map(_.map { doc =>
        Student(
          studentId = doc.getString("studentId"),
          name = doc.getString("name"),
          data_rozhdenya = doc.getString("data_rozhdenya"),
          address = doc.getString("address"),
          email = doc.getString("email"),
          phone_nomer = doc.getString("phone_nomer"),
          god_postuplenya = doc.getString("god_postuplenya"),
          specialnost = doc.getString("specialnost"),
          kurs = Kurs.withName(doc.getString("kurs")),
          status = Status.withName(doc.getString("status")),
          pol = Pol.withName(doc.getString("pol")),
          uroven_angl = Uroven_angl.withName(doc.getString("uroven_angl")),
          ball_Ent = doc.getInteger("ball_Ent"),
          grazhdanstvo = doc.getString("grazhdanstvo")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getStudentById(studentId: String): Future[Option[Student]] = {
    val studentDocument = Document("studentId" -> studentId)

    studentCollection.find(studentDocument).headOption().map {
      case Some(doc) =>
        Some(
          Student(
            studentId = doc.getString("studentId"),
            name = doc.getString("name"),
            data_rozhdenya = doc.getString("data_rozhdenya"),
            address = doc.getString("address"),
            email = doc.getString("email"),
            phone_nomer = doc.getString("phone_nomer"),
            god_postuplenya = doc.getString("god_postuplenya"),
            specialnost = doc.getString("specialnost"),
            kurs = Kurs.withName(doc.getString("kurs")),
            status = Status.withName(doc.getString("status")),
            pol = Pol.withName(doc.getString("pol")),
            uroven_angl = Uroven_angl.withName(doc.getString("uroven_angl")),
            ball_Ent = doc.getInteger("ball_Ent"),
            grazhdanstvo = doc.getString("grazhdanstvo")
          )
        )
      case None => None
    }
  }

  def addStudent(student: Student): Future[String] = {
    val studentDocument = Document(
      "studentId" -> student.studentId,
      "name" -> student.name,
      "data_rozhdenya" -> student.data_rozhdenya,
      "address" -> student.address,
      "email" -> student.email,
      "phone_nomer" -> student.phone_nomer,
      "god_postuplenya" -> student.god_postuplenya,
      "specialnost" -> student.specialnost,
      "kurs" -> student.kurs.toString,
      "status" -> student.status.toString,
      "pol" -> student.pol.toString,
      "uroven_angl" -> student.uroven_angl.toString,
      "ball_Ent" -> student.ball_Ent,
      "grazhdanstvo" -> student.grazhdanstvo
    )

    studentCollection.insertOne(studentDocument).toFuture().map(_ => s"Студент - ${student.name} был добавлен в базу данных.")
  }

  def deleteStudent(studentId: String): Future[String] = {
    val studentDocument = Document("studentId" -> studentId)
    studentCollection.deleteOne(studentDocument).toFuture().map(_ => s"Студент с id ${studentId} был удален из базы данных.")
  }

  def updateStudent(studentId: String, updatedStudent: Student): Future[String] = {
    val filter = Document("studentId" -> studentId)

    val studentDocument = Document(
      "$set" -> Document(
        "studentId" -> updatedStudent.studentId,
        "name" -> updatedStudent.name,
        "data_rozhdenya" -> updatedStudent.data_rozhdenya,
        "address" -> updatedStudent.address,
        "email" -> updatedStudent.email,
        "phone_nomer" -> updatedStudent.phone_nomer,
        "god_postuplenya" -> updatedStudent.god_postuplenya,
        "specialnost" -> updatedStudent.specialnost,
        "kurs" -> updatedStudent.kurs.toString,
        "status" -> updatedStudent.status.toString,
        "pol" -> updatedStudent.pol.toString,
        "uroven_angl" -> updatedStudent.uroven_angl.toString,
        "ball_Ent" -> updatedStudent.ball_Ent,
        "grazhdanstvo" -> updatedStudent.grazhdanstvo
      )
    )

    studentCollection.updateOne(filter, studentDocument).toFuture().map { result =>
      if (result.getMatchedCount > 0) {
        s"Информация о студенте с id ${studentId} была успешно обновлена."
      } else {
        s"Студент с id ${studentId} не найден."
      }
    }
  }
}