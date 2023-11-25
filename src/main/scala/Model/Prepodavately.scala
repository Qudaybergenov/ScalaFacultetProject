package Model
object Pol extends Enumeration {
  type Pol = Value
  val женщина,мужчина = Value
}
object Obrazovanya extends Enumeration {
  type Obrazovanya = Value
  val Бакалвар,Магистрант,Доктарант= Value
}
object Akadem_stepen extends Enumeration {
  type Akadem_stepen = Value
  val Бакалавр,Магистр,Доцент ,Кандидат_наук ,Профессор ,Доктор_наук = Value
}
case class Prepodavately(
                        prepodId:String,
                        fio:String,
                        data_rozhdenya:String,
                        pol:Pol.Pol,
                        akadem_stepen:Akadem_stepen.Akadem_stepen,
                        address:String,
                        email:String,
                        phone_number:String,
                        obrazovanya:Obrazovanya.Obrazovanya,
                        specialyzacia:String,
                        data_prinyatya_narbotu:String
                        )

