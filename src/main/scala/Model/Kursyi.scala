package Model

object KursStatus extends Enumeration {
  type KursStatus = Value
  val Завершено, Активный = Value
}

case class Kursyi(
                   kursId: String,
                   name: String,
                   opicanyia: String,
                   prepodavatel: List[String],
                   spisok_studentov: List[String],
                   mesto_provedenya: String,
                   vremyia_provedenya: String,
                   status: KursStatus.KursStatus,
                   kontaktnayia_info: String
                 )
