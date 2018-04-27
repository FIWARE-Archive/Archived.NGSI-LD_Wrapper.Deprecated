package example

import scala.collection.mutable

object LdInfModelMapper {
  // TODO: Refine this to a proper regular expression as per attribute name rules
  val AnyProp = raw"(.+)".r
  // Reference type to recognize relationships
  val ReferenceAttr = raw"ref(.+)".r

  val UrnPattern = raw"urn:ngsi-ld:(.+):(.+)".r

  def fromNgsi(in: Map[String, Any]) = {
    val out = mutable.Map[String, Any]()

    in.keys.foreach(key => key match {
      case "id" => out += (key -> format_uri(in(key).asInstanceOf[String],
        in("type").asInstanceOf[String]))
      case "type" => out += (key -> in(key))
      case _ => match_key(key, in, out)
    })

    out.toMap[String, Any]
  }

  private def match_key(key: String, in: Map[String, Any], out: mutable.Map[String, Any]): Any = {
    val auxIn = in(key).asInstanceOf[Map[String, Any]]

    key match {
      case "dateCreated" => out += ("createdAt" -> auxIn("value"))
      case "dateModified" => out += ("modifiedAt" -> auxIn("value"))

      case AnyProp(prop) => {
        val nodeType = ((p: String) => {
          var out: (String, String, String) = null

          p match {
            case ReferenceAttr(relName) => out = rel_member(p)
            case _ => out = (p, "Property", "value")
          }
          val declType = auxIn.getOrElse("type", "Property")
          if (declType == "Relationship" || declType == "Reference")
            out = rel_member(p)

          out
        }) (prop)

        val propMap = mutable.Map[String, Any]("type" -> nodeType._2)

        auxIn.keys.foreach(key => key match {
          case "value" => propMap += (nodeType._3 -> format_value(nodeType._2, auxIn("value")))
          case "metadata" => {
            val auxMeta = auxIn("metadata").asInstanceOf[Map[String, Any]]
            auxMeta.keys.foreach(metaKey => {
              val auxMetaProp = auxMeta(metaKey).asInstanceOf[Map[String, Any]]
              metaKey match {
                case "timestamp" => propMap += ("observedAt" -> auxMetaProp("value"))
                case "unitCode" => propMap += ("unitCode" -> auxMetaProp("value"))
                case "entityType" => {
                  val entityId = propMap.getOrElse("object", null).asInstanceOf[String]
                  if (entityId != null) {
                    propMap("object") = format_uri(entityId, auxMetaProp("value").asInstanceOf[String])
                  }
                }
                case _ => match_key(metaKey, auxMeta, propMap)
              }
            })
          }
          case "type" => {}
          case _ => Nil
        })

        out += (nodeType._1 -> propMap.toMap[String, Any])
      }
      case _ => Nil
    }
  }

  private def rel_member(attrName: String) = {
    (attrName, "Relationship", "object")
  }

  private def format_value(nodeType: String, value: Any, entityType: String = null) = {
    var out: Any = value

    if (nodeType == "Relationship") {
      out = format_uri(value.asInstanceOf[String], entityType)
    }

    out
  }

  private def format_uri(id: String, entityType: String) = {
    val eType = if (entityType == null) "Thing" else entityType

    id match {
      case UrnPattern(entType, entId) => if (entType == "Thing") toURN(entId, eType) else id
      case _ => toURN(id, eType)
    }
  }

  def toURN(id: String, entityType: String) = s"urn:ngsi-ld:${entityType}:${id}"

  def parse_urn(urn:String) = {
    urn match {
      case UrnPattern(t,id) => (id,t)
    }
  }

  def toNgsi(in: Map[String, Any]) = {
    val out = mutable.Map[String, Any]()

    out += ("@context" -> "http://example.org/jsonld/ngsi-ld.json")

    in.keys.foreach(key => key match {
      case "id" => out += (key -> in(key))
      case "type" => out += (key -> in(key))
      case _ => match_key_ngsi(key, in, out)
    })

    out.toMap[String, Any]
  }

  private def match_key_ngsi(key: String,
                             in: Map[String, Any],
                             out: mutable.Map[String,Any],
                             parentKey:String=null):mutable.Map[String,Any] = {
    val auxIn = in(key).asInstanceOf[Map[String, Any]]

    val attrMap = mutable.Map[String, Any]()
    val metadata = mutable.Map[String,Any]()

    auxIn.keys.foreach((ikey) => ikey match {
      case "type" => {
        val nodeType = auxIn("type")

        nodeType match {
          case "Property" => attrMap += ("value" -> auxIn.getOrElse("value",null))
          case "Relationship" => {
            val urnObject = auxIn("object")
            attrMap += ("type" -> "Relationship", "value" -> urnObject)
            if (parentKey == null)
              metadata += ("entityType" -> Map("value" -> parse_urn(urnObject.asInstanceOf[String])._2))
          }
          case "GeoProperty" => attrMap += ("type" -> "geo:json", "value" -> auxIn("value"))
          case "TemporalProperty" => attrMap += ("type" -> "DateTime","value" -> auxIn("value"))
          case _ => throw new Exception("Node type not provided")
        }
      }
      case "observedAt" => metadata += ("timestamp" -> Map("value" -> auxIn(ikey), "type" -> "DateTime"))
      case "unitCode" => metadata += ("unitCode" -> Map("value" -> auxIn(ikey)))
      case "value" => Nil
      case "object" => Nil
      case _ => {
        match_key_ngsi(ikey,auxIn,metadata,key)
      }
    })

    if (metadata.size > 0)
      attrMap += ("metadata" -> metadata.toMap[String,Any])

    out += (key -> attrMap.toMap[String,Any])
  }
}