package com.vikram.core

import RedditDataModel.{OAuthTokenResponse, RedditListing, RedditListingElement, RedditListingThing}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait CustomJsonProtocols extends SprayJsonSupport with DefaultJsonProtocol {
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