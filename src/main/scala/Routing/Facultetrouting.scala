package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.FacultetRepository
import Model._
class FacultetRoutes(implicit val facultetRepository: FacultetRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats
  val route =
    pathPrefix("facultet") {
      concat(
        pathEnd {
          concat(
            get {
              complete(facultetRepository.getAllFacultets())
            },
            post {
              entity(as[Facultet]) { facultet =>
                complete(facultetRepository.addFacultet(facultet))
              }
            }
          )
        },
        path(Segment) { facultetId =>
          concat(
            get {
              complete(facultetRepository.getFacultetById(facultetId))
            },
            put {
              println("revsd")
              entity(as[Facultet]) { updatedFacultet =>
                println("revsd")

                complete(facultetRepository.updateFacultet(facultetId, updatedFacultet))
              }
            },
            delete {
              complete(facultetRepository.deleteFacultet(facultetId))
            }
          )
        }
      )
    }
}