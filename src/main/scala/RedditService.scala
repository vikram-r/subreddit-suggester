import java.awt.Desktop
import java.net.URI
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import spray.http.OAuth2BearerToken
import spray.json._
import DefaultJsonProtocol._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object RedditService {

}

class RedditService {
  import ExecutionContext.Implicits.global

  val apiWrapper = new RedditApiWrapper

  /**
    * Obtain OAuth2 permissions for a reddit account (the user specifies in step 3)
    *
    * 1. Get the authorization token by GETing
    *    https://www.reddit.com/api/v1/authorize?client_id=CLIENT_ID&response_type=TYPE&
    *    state=RANDOM_STRING&redirect_uri=URI&duration=DURATION&scope=SCOPE_STRING
    *
    * 2. The response contains a redirect URL (set in the reddit app), which should be opened
    *    in the web browser
    *
    * 3. Wait for user input (to accept requested permissions).
    *
    * 4. On accept, the user will be redirected to the redirect endpoint specified in the reddit app.
    *    The redirect url contains "code" as a query parameter, which should be extracted.
    *
    * 5. Get the access token and refresh_token by POSTing https://www.reddit.com/api/v1/access_token.
    *    The request needs to include the reddit app client id/client secret as the username/password in
    *    the HTTP basic authorization header. It also needs to include grant_type (use permanent), code
    *    (from step 2), and redirect_uri (same one as step 2) in the form body.
    *
    * 6. The response contains json with access_token and refresh_token, which should be saved.
    *
    *
    * All OAuth2 requests in the future should use "Authorization: bearer <access_token>" header. When
    * the access_token expires, POST to the same URL in step 5 except set
    *
    *   grant_type=refresh_token <- the actual string "refresh_token"
    *   refresh_token=<refresh_token> <- the value of refresh_token
    *
    * which returns the same JSON blob with a new access_token.
    */

  def oAuthRequestPermissions(): Unit = {
    val response = apiWrapper.authorizeUser()
    for {
      authResponse ← response //step 1
      redirectUrl ← authResponse.headers.find(_.name == "Location").map(_.value)
    } {
      println(redirectUrl)

      //todo this is kind of lame, but it might be necessary. Need to figure out a good way to have a listener on the callback url
      if(Desktop.isDesktopSupported) {
        Desktop.getDesktop.browse(new URI(redirectUrl))
      }
      println("Pass the 'code' query parameter from the redirect url to this program by running `gradle run -Dcode=123` ")
      //todo store the refresh_token and access_token in database, so process doesn't have to be repeated more than once
    }
    //todo need to handle these futures better. This way still doesn't work
    Await.result(response, Duration(10000, TimeUnit.MILLISECONDS)) //oauth login process needs to be sequential
  }

  def oAuthGetToken(code: String): Option[String] = {
    val response = apiWrapper.retreiveAccessToken(code)
    for (tokenResponse ← response) {

//      val json = tokenResponse.toJson
//      println(json)
      println(tokenResponse.message.entity)
    }

    Await.result(response, Duration(10000, TimeUnit.MILLISECONDS)) //oauth login process needs to be sequential
    Some("")
  }

  def getSubscribedSubreddits()(implicit token: OAuth2BearerToken): List[String] = {
    for (response ← apiWrapper.getSubscribedSubreddits) {
      println(response.message)
    }
    List.empty
  }
}
