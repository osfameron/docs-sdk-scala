// #tag::imports[]
import java.util.UUID

import com.couchbase.client.scala.Cluster
import com.couchbase.client.scala.json.{JsonObject, JsonObjectSafe}

import scala.util.{Failure, Success, Try}
// #end::imports[]

object ClusterExample {
  def main(args: Array[String]) {
    // #tag::cluster[]
    val cluster = Cluster.connect("10.112.180.101", "username", "password").get
    // #end::cluster[]

    // #tag::resources[]
    val bucket = cluster.bucket("bucket-name")
    val collection = bucket.defaultCollection
    // #end::resources[]

    // #tag::json[]
    val json = JsonObject("status" -> "awesome")
    // #end::json[]

    // #tag::upsert[]
    val docId = UUID.randomUUID().toString
    collection.upsert(docId, json) match {
      case Success(result) =>
      case Failure(exception) => println("Error: " + exception)
    }
    // #end::upsert[]

    // #tag::get[]
    // Get a document
    collection.get(docId) match {
      case Success(result) =>

        // Convert the content to a JsonObjectSafe
        result.contentAs[JsonObjectSafe] match {
          case Success(json) =>

            // Pull out the JSON's status field, if it exists
            json.str("status") match {
              case Success(hello) => println(s"Couchbase is $hello")
              case _ => println("Field 'status' did not exist")
            }
          case Failure(err) => println("Error decoding result: " + err)
        }
      case Failure(err) => println("Error getting document: " + err)
    }
    // #end::get[]

    def getFor() {
      // #tag::get-for[]
      val result: Try[String] = for {
        result <- collection.get(docId)
        json <- result.contentAs[JsonObjectSafe]
        status <- json.str("status")
      } yield status

      result match {
        case Success(status) => println(s"Couchbase is $status")
        case Failure(err) => println("Error: " + err)
      }
      // #end::get-for[]
    }

    def getMap() {
      // #tag::get-map[]
      val result: Try[String] = collection.get(docId)
        .flatMap(_.contentAs[JsonObjectSafe])
        .flatMap(_.str("status"))

      result match {
        case Success(status) => println(s"Couchbase is $status")
        case Failure(err) => println("Error: " + err)
      }
      // #end::get-map[]
    }
  }
}