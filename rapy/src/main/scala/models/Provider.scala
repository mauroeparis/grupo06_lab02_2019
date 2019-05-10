package models

object Provider extends ModelCompanion[Provider] {
  protected def dbTable: DatabaseTable[Provider] = Database.providers

   def apply(username: String, storeName: String, locationId: Int,
             maxDeliveryDistance: Int): Provider =
     new Provider(username, storeName, locationId, maxDeliveryDistance)

   private[models] def apply(jsonValue: JValue): Provider = {
     val value = jsonValue.extract[Provider]
     value._id = (jsonValue \ "id").extract[Int]
     value.save()
     value
   }
 }

class Provider(val username: String, val storeName: String,
               val locationId: Int,
               val maxDeliveryDistance: Int) extends Model[Provider] {
  protected def dbTable: DatabaseTable[Provider] = Provider.dbTable

  override def toMap: Map[String, Any] = super.toMap + (
    "username" -> username, "storeName" -> storeName,
    "locationId" -> locationId,
    "maxDeliveryDistance" -> maxDeliveryDistance
  )

  override def toString: String = s"Provider: $username"
}
