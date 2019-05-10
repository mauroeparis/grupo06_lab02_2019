package models

object Consumer extends ModelCompanion[Consumer] {
  protected def dbTable: DatabaseTable[Consumer] = Database.consumers

   def apply(username: String, locationId: Int, balance: Float = 0): Consumer =
     new Consumer(username, locationId, balance)

   private[models] def apply(jsonValue: JValue): Consumer = {
     val value = jsonValue.extract[Consumer]
     value._id = (jsonValue \ "id").extract[Int]
     value.save()
     value
   }
 }

class Consumer(val username: String, val locationId: Int, var balance: Float = 0) extends Model[Consumer] {
  protected def dbTable: DatabaseTable[Consumer] = Consumer.dbTable

  override def toMap: Map[String, Any] = super.toMap + (
    "username" -> username, "locationId" -> locationId, "balance" -> balance
  )

  override def toString: String = s"Consumer: $username"

  def updateBalance(orderCost: Float): Unit = {
    this.balance = this.balance - orderCost
  }
}
