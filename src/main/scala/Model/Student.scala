package Model
object Kurs extends Enumeration {
  type Kurs = Value
  val Первый,Второй,Третий,Четвертый = Value
}
object Status extends Enumeration {
  type Status = Value
  val Активный,Отчислен,Выпускник = Value
}
object Uroven_angl extends Enumeration {
  type Uroven_angl = Value
  val Beginner,Elementary,Intermediate,Upper_Intermediate,Advanced,Proficiency = Value
}

case class Student(
                  studentId:String,
                  name:String,
                  data_rozhdenya:String,
                  address:String,
                  email:String,
                  phone_nomer:String,
                  god_postuplenya:String,
                  specialnost:String,
                  kurs:Kurs.Kurs,
                  status:Status.Status,
                  pol:Pol.Pol,
                  uroven_angl:Uroven_angl.Uroven_angl,
                  ball_Ent:Int,
                  grazhdanstvo:String
                  )
