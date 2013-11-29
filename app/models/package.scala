import play.api.libs.iteratee.Enumerator
import play.api.libs.json.JsValue

/**
 * Sealed case classes for messages to and from actors
 */
package object models {

  sealed case class Connect()
  sealed case class Connected(enumerator: Enumerator[JsValue])
  sealed case class NewTerms(terms: List[String])
  sealed case class GetCurrentTerms()

}
