package Model

case class Nauchnyi_center(
                           centerId:String,
                           name:String,
                           director:String,
                           opicanya:String,
                           spisok_proektov:List[String],
                           spisok_studentov:List[String],
                           kontaktnayia_info:String
                           )
