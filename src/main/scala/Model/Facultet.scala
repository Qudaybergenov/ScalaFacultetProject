package Model

import java.util.Date

case class Facultet(
                     facultetId:String,
                     name:String,
                     spisok_kafedr:List[String],
                     spisok_studentof:List[String],
                     raspolozhenya:String,
                     god_osnovanya:String,
                     kontaktnyi_tel:String,
                     email:String,
                     spisok_sportivnyx_komand:List[String],
                     spisok_nauchnyx_center:List[String],
                     spisok_kursov:List[String],
                   )
