package models

object Consumer extends ModelCompanion[Consumer] {
  protected def dbTable: DatabaseTable[Consumer] = Database.consumers

   def apply(username: String, locationName: String): Consumer =
     new Consumer(username, locationName)

   private[models] def apply(jsonValue: JValue): Consumer = {
     val value = jsonValue.extract[Consumer]
     value._id = (jsonValue \ "id").extract[Int]
     value.save()
     value
   }
 }

class Consumer(val username: String, val locationName: String) extends Model[Consumer] {
  protected def dbTable: DatabaseTable[Consumer] = Consumer.dbTable

  override def toMap: Map[String, Any] = super.toMap + (
    "username" -> username, "locationName" -> locationName
  )

  override def toString: String = s"Consumer: $username"
}
