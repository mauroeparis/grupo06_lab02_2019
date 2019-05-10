package models

object Provider extends ModelCompanion[Provider] {
  protected def dbTable: DatabaseTable[Provider] = Database.providers

   def apply(username: String, storeName: String, locationName: String,
             maxDeliveryDistance: Int, balance: Float = 0): Provider =
     new Provider(username, storeName, locationName, maxDeliveryDistance, balance)

   private[models] def apply(jsonValue: JValue): Provider = {
     val value = jsonValue.extract[Provider]
     value._id = (jsonValue \ "id").extract[Int]
     value.save()
     value
   }
 }

class Provider(val username: String, val storeName: String,
               val locationName: String,
               val maxDeliveryDistance: Int,
               var balance: Float = 0) extends Model[Provider] {
  protected def dbTable: DatabaseTable[Provider] = Provider.dbTable

  override def toMap: Map[String, Any] = super.toMap + (
    "username" -> username, "storeName" -> storeName,
    "locationName" -> locationName,
    "maxDeliveryDistance" -> maxDeliveryDistance,
    "balance" -> balance
  )

  override def toString: String = s"Provider: $username"

  def updateBalance(orderCost: Float): Unit = {
    this.balance = this.balance + orderCost
  }
}
