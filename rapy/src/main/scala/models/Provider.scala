package models

object Provider extends ModelCompanion[Provider] {
  protected def dbTable: DatabaseTable[Provider] = Database.providers

   def apply(username: String, locationName: String): Provider =
     new Provider(username, locationName)

   private[models] def apply(jsonValue: JValue): Provider = {
     val value = jsonValue.extract[Provider]
     value._id = (jsonValue \ "id").extract[Int]
     value.save()
     value
   }
 }

class Provider(val username: String, val locationName: String) extends Model[Provider] {
  protected def dbTable: DatabaseTable[Provider] = Provider.dbTable

  override def toMap: Map[String, Any] = super.toMap + (
    "username" -> username, "locationName" -> locationName
  )

  override def toString: String = s"Provider: $username"
}
