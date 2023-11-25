package Model

case class Kafedra(
                  kafedraId:String,
                  name:String,
                  dekan:String,
                  spisok_prepodovatelei:List[String],
                  spisok_studentov:List[String],
                  kontaktnaya_info:String
                  )
