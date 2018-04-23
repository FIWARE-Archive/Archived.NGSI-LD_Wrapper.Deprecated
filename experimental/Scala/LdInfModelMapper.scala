package example

import scala.collection.mutable

object LdInfModelMapper {
  // TODO: Refine this to a proper regular expression as per attribute name rules
  val AnyProp = raw"(.+)".r
  // Reference type to recognize relationships
  val ReferenceAttr = raw"ref(.+)".r

  val UrnPattern = raw"urn:ngsi-ld:(.+):(.+)".r

  def fromNgsi(in:Map[String,Any]) = {
    val out = mutable.Map[String,Any]()

    in.keys.foreach(key => key match {
      case "id" => out += (key -> format_uri(in(key).asInstanceOf[String],
        in("type").asInstanceOf[String]))
      case "type" => out += (key -> in(key))
      case _ => match_key(key,in,out)
    })

    out.toMap[String,Any]
  }

  private def match_key(key:String,in:Map[String,Any],out:mutable.Map[String,Any]):Any = {
    val auxIn = in(key).asInstanceOf[Map[String,Any]]

    key match {
      case "dateCreated" => out += ("createdAt" -> auxIn("value"))
      case "dateModified" => out += ("modifiedAt" -> auxIn("value"))

      case AnyProp(prop) => {
        val nodeType = ((p:String) => {
          var out:(String,String,String) = null

          p match {
            case ReferenceAttr(relName) =>  out = rel_member(p)
            case _ => out = (p,"Property","value")
          }
          val declType = auxIn.getOrElse("type","Property")
          if (declType == "Relationship" || declType == "Reference")
            out = rel_member(p)

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
                case "entityType" => {
                  val entityId = propMap.getOrElse("object",null).asInstanceOf[String]
                  if (entityId != null) {
                    propMap("object") = format_uri(entityId,auxMetaProp("value").asInstanceOf[String])
                  }
                }
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

  private def rel_member(attrName:String) = {
    (attrName,"Relationship","object")
  }

  private def format_value(nodeType:String,value:Any,entityType:String=null) = {
    var out:Any = value

    if (nodeType == "Relationship") {
      out = format_uri(value.asInstanceOf[String],entityType)
    }

    out
  }

  private def format_uri(id:String,entityType:String) = {
    val eType = if (entityType == null) "Thing" else entityType

     id match {
      case UrnPattern(entType,entId) => if (entType == "Thing") toURN(entId, eType) else id
      case _ => toURN(id,eType)
    }
  }

  def toURN(id:String,entityType:String) = s"urn:ngsi-ld:${entityType}:${id}"

  def toNgsi(in:Map[String,Any]) = {

  }
}
