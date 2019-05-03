package models

trait ModelCompanion[M <: Model[M]] {
  protected def dbTable: DatabaseTable[M]

  private[models] def apply(jsonValue: JValue): M

  def all: List[M] = dbTable.instances.values.toList

  def find(id: Int): Option[M] = {
    val objList = dbTable.instances.values.toList
    if (exists("id", id)) {
      Some (objList.filter(_.id == id).head)
    } else {
      None
    }
  }

  def exists(attr: String, value: Any): Boolean = {
    if (!filter(Map(attr -> value)).isEmpty) {
      return true
    } else {
      return false
    }
  }

  def delete(id: Int): Unit = {
    dbTable.delete(id)
  }

  def filter(mapOfAttributes: Map[String, Any]): List[M] = {
    val objList = dbTable.instances.values.toList
    objList.filter(
      mapOfAttributes.toSet subsetOf _.toMap.toSet
    )
  }
}

trait Model[M <: Model[M]] { self: M =>
  protected var _id: Int = 0

  def id: Int = _id

  protected def dbTable: DatabaseTable[M]

  def toMap: Map[String, Any] = Map("id" -> _id)

  def save(): Unit = {
    if (_id == 0) { _id = dbTable.getNextId }
    dbTable.save(this)
  }
}
