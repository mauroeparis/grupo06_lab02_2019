package models

object Consumer extends ModelCompanion[Consumer] {
  protected def dbTable: DatabaseTable[Consumer] = Database.consumers

   def apply(username: String, locationName: String, balance: Float = 0): Consumer =
     new Consumer(username, locationName, balance)

   private[models] def apply(jsonValue: JValue): Consumer = {
     val value = jsonValue.extract[Consumer]
     value._id = (jsonValue \ "id").extract[Int]
     value.save()
     value
   }
 }

class Consumer(val username: String, val locationName: String, var balance: Float = 0) extends Model[Consumer] {
  protected def dbTable: DatabaseTable[Consumer] = Consumer.dbTable

  override def toMap: Map[String, Any] = super.toMap + (
    "username" -> username, "locationName" -> locationName, "balance" -> balance
  )

  override def toString: String = s"Consumer: $username"

  def updateBalance(orderCost: Float): Unit = {
    this.balance = this.balance - orderCost
  }
}
