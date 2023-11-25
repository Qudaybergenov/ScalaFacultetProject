package Model
object Vid_sporta extends Enumeration {
  type Vid_sporta = Value
  val Футбол,Баскетбол,Волейбол,Теннис,Бокс,Борьба,Плавание,Гимнастика,Лыжи,Теннис_настольный = Value
}
case class Sport_komand(
                       komandID:String,
                       name:String,
                       vid_sporta:Vid_sporta.Vid_sporta,
                       trener:String,
                       sostav:List[String],
                       dostyzhenyia:String,
                       populyiarnost:String,
                       raspisanyia:String,

                       )
