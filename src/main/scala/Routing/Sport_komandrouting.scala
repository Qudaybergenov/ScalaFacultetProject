package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.SportKomandRepository
import Model._

class SportKomandRoutes(implicit val sportKomandRepository: SportKomandRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("sportkomandy") {
      concat(
        pathEnd {
          concat(
            get {
              complete(sportKomandRepository.getAllSportKomands())
            },
            post {
              entity(as[Sport_komand]) { sportKomand =>
                complete(sportKomandRepository.addSportKomand(sportKomand))
              }
            }
          )
        },
        path(Segment) { komandID =>
          concat(
            get {
              complete(sportKomandRepository.getSportKomandById(komandID))
            },
            put {
              entity(as[Sport_komand]) { updatedSportKomand =>
                complete(sportKomandRepository.updateSportKomand(komandID, updatedSportKomand))
              }
            },
            delete {
              complete(sportKomandRepository.deleteSportKomand(komandID))
            }
          )
        }
      )
    }
}