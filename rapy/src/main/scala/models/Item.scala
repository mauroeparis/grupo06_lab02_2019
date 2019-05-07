package models

object Item extends ModelCompanion[Item] {
  protected def dbTable: DatabaseTable[Item] = Database.items

   def apply(name: String, description: String, price: Float,
             providerUsername: String): Item =
     new Item(name, description, price, providerUsername)

   private[models] def apply(jsonValue: JValue): Item = {
     val value = jsonValue.extract[Item]
     value._id = (jsonValue \ "id").extract[Int]
     value.save()
     value
   }
 }

class Item(val name: String, val description: String, val price: Float,
           val providerUsername: String) extends Model[Item] {
  protected def dbTable: DatabaseTable[Item] = Item.dbTable

  override def toMap: Map[String, Any] = super.toMap + (
    "name" -> name, "description" -> description, "price" -> price,
    "providerUsername" -> providerUsername
  )

  override def toString: String = s"Item: $name"
}
