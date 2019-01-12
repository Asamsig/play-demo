package models

import play.api.libs.json.Json

case class Person(name: String, interests: Set[String])

object Person {
  implicit val personFormat = Json.format[Person]
}
