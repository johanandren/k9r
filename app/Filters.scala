import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter

/**
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
class Filters extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(global.BasicAuthFilter)
}
