package example

import scala.collection.mutable

object LdInfModelMapper {
  // TODO: Refine this to a proper regular expression as per attribute name rules
  val AnyProp = raw"(.+)".r
  // Reference type to recognize relationships
  val ReferenceAttr = raw"ref(.+)".r

  def fromNgsi(in:Map[String,Any]) = {
    val out = mutable.Map[String,Any]()

    in.keys.foreach(key => key match {
      case "id" => out += (key -> s"urn:ngsi-ld:${in("type")}:${in(key)}")
      case "type" => out += (key -> in(key))
      case _ => match_key(key,in,out)
    })

    /* did not work for me
    in.foreach(tuple => tuple match {
      case ("id",StrValue) => out += tuple
      case ("type",StrValue) => out += tuple
      case _ => Nil
    }) */

    out.toMap[String,Any]
  }

  def match_key(key:String,in:Map[String,Any],out:mutable.Map[String,Any]):Any = {
    val auxIn = in(key).asInstanceOf[Map[String,Any]]

    key match {
      case "dateCreated" => out += ("createdAt" -> auxIn("value"))
      case "dateModified" => out += ("modifiedAt" -> auxIn("value"))

      case AnyProp(prop) => {
        val nodeType = ((p:String) => {
          var out:(String,String,String) = null;

          p match {
            case ReferenceAttr(relName) => out = rel_member(relName)
            case _ => out = (p,"Property","value")
          }
          val declType = auxIn.getOrElse("type","Property")
          if (declType == "Relationship" || declType == "Reference") {
            out = rel_member(p)
          }

          out
        })(prop)

        val propMap = mutable.Map[String, Any]("type" -> nodeType._2)

        auxIn.keys.foreach(key => key match {
          case "value" => propMap += (nodeType._3 -> format_value(nodeType._2,auxIn("value")))
          case "metadata" => {
            val auxMeta = auxIn("metadata").asInstanceOf[Map[String, Any]]
            auxMeta.keys.foreach(metaKey => {
              val auxMetaProp = auxMeta(metaKey).asInstanceOf[Map[String, Any]]
              metaKey match {
                case "timestamp" => propMap += ("observedAt" -> auxMetaProp("value"))
                case "unitCode" => propMap += ("unitCode" ->  auxMetaProp("value"))
                case _ => match_key(metaKey, auxMeta, propMap)
              }
            })
          }
          case "type" => { }
          case _ => Nil
        })

        out += (nodeType._1 -> propMap.toMap[String,Any])
      }
      case _ => Nil
    }
  }

  def rel_member(attrName:String) = {
    (attrName,"Relationship","object")
  }

  def format_value(nodeType:String,value:Any) = {
    var out:Any = value

    if (nodeType == "Relationship") {
      if (!value.asInstanceOf[String].startsWith("urn")) {
        out = s"urn:ngsi-ld:${value}"
      }
    }

    out
  }

  def toNgsi(in:Map[String,Any]) = {

  }

  def main(args: Array[String]) = {
    val testData = Map("id"->"myId", "type" -> "myType",
      "refOther" -> Map("type"->"Relationship","value" -> "anId"),
      "dateCreated" -> Map("value" -> "2018-04-23T12:00:00", "type" -> "DateTime"),
      "speed" -> Map("value"->100,
                      "metadata" -> Map("accuracy" -> Map("value" -> 0.89),
                                        "timestamp" -> Map("value" -> "2018-04-23T12:00:00",
                                                            "type" -> "DateTime"),
                                        "providedBy" -> Map("type" -> "Reference", "value" -> "4567"))))

    val x = JsonSerializer.serialize((LdInfModelMapper.fromNgsi(testData)))
    println(x)
  }
}
