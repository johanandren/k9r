/**
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
package global

import sun.misc.BASE64Decoder
import play.api.mvc._
import scala.concurrent.Future
import play.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.util.Try

object BasicAuthFilter extends Filter {
  private val realm = "kickstartr"
  private val unauthResult = Results.Unauthorized.withHeaders(("WWW-Authenticate", s"""Basic realm="$realm""""))
  private val username = sys.env.getOrElse("K9R_USERNAME", "user")
  private val password = sys.env.getOrElse("K9R_PASSWORD", "password")

  def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    requestHeader.headers.get("authorization").flatMap(decodeBasicAuth) match {
      case Some((user, pass)) if username == user && password == pass =>
        nextFilter(requestHeader)

      case _ => Future.successful(unauthResult)
    }
  }

  private val HeaderFormat = """Basic (.+)""".r
  private def decodeBasicAuth(auth: String): Option[(String, String)] =
    Try {
      auth match {
        case HeaderFormat(encoded) =>
          val decoded = new String(new BASE64Decoder().decodeBuffer(encoded), "UTF-8")
          val List(username, pass) = decoded.split(":", 2).toList
          Some((username, pass))

        case _ => None
      }
    }.getOrElse(None)

}