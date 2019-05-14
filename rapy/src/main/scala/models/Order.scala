package models
import upickle.default.{ReadWriter => RW, macroRW}


case class OrderItem (name: String, amount: Int)
object OrderItem{
  implicit val rw: RW[OrderItem] = macroRW
}


object Order extends ModelCompanion[Order] {
  protected def dbTable: DatabaseTable[Order] = Database.orders

   implicit def string2Option(s: String) = Some(s)

   def apply(providerUsername: String,
            consumerUsername: String,
            items: List[OrderItem],
            status: String = "Payed"): Order = new Order(
                providerUsername,
                consumerUsername,
                items,
                status
            )

   private[models] def apply(jsonValue: JValue): Order = {
     val value = jsonValue.extract[Order]
     value._id = (jsonValue \ "id").extract[Int]
     value.save()
     value
   }
 }

class Order(val providerUsername: String,
            val consumerUsername: String,
            val items: List[OrderItem],
            var status: String = "Payed") extends Model[Order] {

  protected def dbTable: DatabaseTable[Order] = Order.dbTable

  override def toMap: Map[String, Any] = super.toMap + (
    "providerUsername" -> providerUsername,
    "consumerUsername" -> consumerUsername,
    "items" -> items,
    "status" -> status,
  )

  override def toString: String = s"Order #$id from $consumerUsername to $providerUsername"

  def getTotal(): Float = {
    this.items.flatMap(
      orderItem => Item.filter(
        Map(
          "providerId" -> Provider.filter(
            Map("username" -> this.providerUsername)
          ).head.id,
          "name" -> orderItem.name
        )
      ).map(item => item.price * orderItem.amount)
    ).sum
  }
}
