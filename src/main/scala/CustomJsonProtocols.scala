import spray.json._
import RedditDataModel._

trait CustomJsonProtocols extends DefaultJsonProtocol {
  implicit val oAuthTokenResponseFormat = jsonFormat5(OAuthTokenResponse)

  implicit object redditListingElementFormat extends RootJsonFormat[RedditListingElement] {
    override def read(json: JsValue): RedditListingElement = {
      json.asJsObject.getFields("kind", "data") match {
        case Seq(JsString(kind), JsObject(data)) ⇒
          new RedditListingElement(
            kind = kind,
            data = data
          )
        case _  ⇒ throw DeserializationException("RedditListingElement expected")
      }
    }
    override def write(obj: RedditListingElement): JsValue =
      JsObject("kind" → JsString(obj.kind), "data" → JsObject(obj.data))
  }

  implicit val redditListingFormat = jsonFormat4(RedditListing)

  implicit val redditListingThingFormat = jsonFormat2(RedditListingThing)

}

object CustomJsonProtocols extends CustomJsonProtocols {}