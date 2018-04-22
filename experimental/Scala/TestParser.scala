package example

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

import scala.collection.mutable

object TestParser extends JSONParser {
  def main(args: Array[String]) = {

    val data = getData("http://localhost:1026")

    val currentTime = System.currentTimeMillis()

    parse(value, data) match {
      case Success(matched,_) => {
        val entities = matched.asInstanceOf[List[Map[String,Any]]]
        println(s"Time spent: ${System.currentTimeMillis() - currentTime}")

        println(matched)
        println(entities.size)
        val otherMap = mutable.Map[String,Any]() ++= entities(0).toSeq
        println(otherMap)
        val vall = otherMap("type")
        println(vall)
      }
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }
  }

  def getData(endpoint:String,tenant:String="") = {
    val getRequest = new HttpGet(endpoint + "/v2/entities")

    if (tenant.length > 0) {
      getRequest.setHeader("Fiware-Service", tenant)
    }

    // send the GET request
    val httpClient = HttpClientBuilder.create().build()
    val result = httpClient.execute(getRequest)
    println (s"HTTStatus: ${result.getStatusLine.getStatusCode}")
    println (s"${result.getHeaders("Content-Type")(0).getValue}")

    EntityUtils.toString(result.getEntity, "UTF-8")
  }
}
