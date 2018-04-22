package example

object TestParser3 extends JSONParser {
  def main(args: Array[String]) = {

    val data = """{ "x": 56, "w": "z"}"""
    val data2 = """"hola""""

    val currentTime = System.currentTimeMillis()

    parse(value, data) match {
      case Success(matched,_) => {
        /*
        val m = matched.asInstanceOf[Map[String,Any]]
        val m2 = mutable.HashMap[String,Any]()
        m.foreach(e => {
          println(e._1 + " " + e._2)
          m2.put(e._1,e._2)
          m2.put("z", "34")
        })

        println(m2(""""x"""")) */
        val s = "hola"
        println(matched)
        println(s)
      }
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }
  }
}
