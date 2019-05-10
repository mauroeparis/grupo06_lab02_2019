package app

import cask._
import models._

object RestfulAPIServer extends MainRoutes  {
  override def host: String = "0.0.0.0"
  override def port: Int = 4000

  @get("/")
  def root(): Response = {
    JSONResponse("Ok")
  }

  @get("/api/locations")
  def locations(): Response = {
    JSONResponse(Location.all.map(location => location.toMap))
  }

  @postJson("/api/locations")
  def locations(name: String, coordX: Double, coordY: Double): Response = {
    if (Location.exists("name", name)) {
      return JSONResponse("Existing location", 409)
    }

    val location = Location(name, coordX, coordY)
    location.save()
    JSONResponse(location.id)
  }

  @get("/api/consumers")
  def consumers(): Response = {
    JSONResponse(Consumer.all.map(consumer => consumer.toMap))
  }

  @postJson("/api/consumers")
  def consumers(username: String, locationName: String): Response = {
    if (Consumer.exists("username", username) || Provider.exists("username", username)) {
      return JSONResponse("Existing consumer", 409)
    } else if (!Location.exists("name", locationName)) {
      return JSONResponse("Non existing location", 404)
    }

    val consumer = Consumer(username, locationName)
    consumer.save()
    JSONResponse(consumer.id)
  }

  @get("/api/providers")
  def providers(locationName: String = ""): Response = locationName match {
    case "" => JSONResponse(
      Provider.all.map(provider => provider.toMap)
    )
    case locationName =>
      if (!Location.exists("name", locationName)) {
        JSONResponse("Non existing location", 404)
      } else {
        val locationId :Int = Location.filter(Map("name" -> locationName)).head.id
        JSONResponse(
          Provider.filter(Map("locationId" ->
            locationId)).map(provider => provider.toMap))
      }
  }

  @postJson("/api/providers")
  def providers(username: String, storeName: String, locationId: Int,
                maxDeliveryDistance: Int): Response = {
    if (Provider.exists("username", username) || Consumer.exists("username", username)) {
      return JSONResponse("Existing username", 409)
    } else if (maxDeliveryDistance < 0) {
      return JSONResponse("Negative maxDeliveryDistance", 400)
    } else if (Provider.exists("storeName", storeName)) {
      return JSONResponse("Existing storeName", 409)
    } else if (!(Location.exists("id", locationId))) {
      return JSONResponse("Non existing location", 404)
    }

    val provider = Provider(username, storeName, locationId,
                            maxDeliveryDistance)
    provider.save()
    JSONResponse(provider.id)
  }

  @get("/api/items")
  def items(providerUsername: String = ""): Response = providerUsername match {
    case "" => JSONResponse(
        Item.all.map(item => item.toMap)
    )
    case providerUsername =>
      if (!Provider.exists("username", providerUsername)) {
        return JSONResponse("Non existing provider", 404)
      } else {
        JSONResponse(
          Item.filter(Map("providerUsername" ->
            providerUsername)).map(item => item.toMap)
        )
      }
  }

  @postJson("/api/items")
  def items(name: String, description: String, price: Float,
            providerUsername: String): Response = {
      if (price < 0) {
        return JSONResponse("Negative price", 400)
      } else if (!Provider.exists("username", providerUsername)) {
        return JSONResponse("Non existing provider", 404)
      } else if (!(Item.filter(Map("name" -> name,
                 "providerUsername" ->providerUsername))).isEmpty) {
        return JSONResponse("Existing item for provider", 409)
      }

    val item = Item(name, description, price, providerUsername)
    item.save()
    JSONResponse(item.id)
  }

  @post("/api/items/delete/:id")
  def itemsDelete(id: Int): Response = {
    if (Item.exists("id", id)) {
      Item.delete(id)
      return JSONResponse("Ok", 200)
    } else {
      return JSONResponse("non existing item", 404)
    }
  }

  @post("/api/users/delete/:username")
  def userDelete(username: String): Response = {
    if (Provider.exists("username", username)) {
      Order.filter(Map("providerUsername" -> username)).map(
        order => Order.delete(order.id)
      )
      Item.filter(Map("providerUsername" -> username)).map(
        item => Item.delete(item.id)
      )
      Provider.filter(Map("username" -> username)).map(
        provider => Provider.delete(provider.id)
      )
      return JSONResponse("Ok", 200)
    } else if (Consumer.exists("username", username)) {
      Item.filter(Map("providerUsername" -> username)).map(
        item => Item.delete(item.id)
      )
      Consumer.filter(Map("username" -> username)).map(
        consumer => Consumer.delete(consumer.id)
      )
      return JSONResponse("Ok", 200)
    } else {
      JSONResponse(
        "non existing user", 404
      )
    }
  }

  @postJson("/api/orders")
  def orders(providerUsername: String,
            consumerUsername: String, items: List[OrderItem]): Response = {
    if (!Provider.exists("username", providerUsername) ||
        !Consumer.exists("username", consumerUsername)) {
      return JSONResponse(
        "non existing consumer/provider/item for provider", 404)
    } else if (!items.filter(_.amount<0).isEmpty) {
      return JSONResponse("negative amount", 400)
    }

    val order = Order(providerUsername, consumerUsername, items)
    order.save()
    JSONResponse(order.id)
  }

  @get("/api/orders")
  def orders(username: String): Response = {
    if (Provider.exists("username", username)) {
      JSONResponse(
        Order.filter(Map("providerUsername" -> username)).map(order =>
                                                              order.toMap)
      )
    } else if (Consumer.exists("username", username)) {
      JSONResponse(
        Order.filter(Map("consumerUsername" -> username)).map(order =>
                                                              order.toMap)
      )
    } else {
      JSONResponse(
        "non existing user", 404
      )
    }
  }

  @post("/api/orders/delete/:id")
  def ordersDelete(id: Int): Response = {
    if (Order.exists("id", id)) {
      Order.delete(id)
      return JSONResponse("Ok", 200)
    } else {
      return JSONResponse("Non existing order", 404)
    }
  }

  @post("/api/orders/deliver/:id")
  def ordersDeliver(id: Int): Response = {
    Order.find(id) match {
      case None => JSONResponse("Non existing order", 404)
      case Some(order) =>
        if (order.status == "Delivered") {
          JSONResponse("Order already delivered", 400)
        } else {
          order.status = "Delivered"

          Consumer.filter(
            Map("username" -> order.consumerUsername)
          ).map(
            consumer => consumer.updateBalance(order.getTotal)
          )

          Provider.filter(
            Map("username" -> order.providerUsername)
          ).map(
            provider => provider.updateBalance(order.getTotal)
          )

          JSONResponse("Ok", 200)
        }
    }
  }

  @get("/api/orders/detail/:id")
  def ordersDetail(id: Int): Response = {
    Order.find(id) match {
      case None => JSONResponse("Non existing order", 404)
      case Some(order) => JSONResponse(
        order.items.flatMap(
          orderItem => Item.filter(
            Map(
              "name" -> orderItem.name,
              "providerUsername" -> order.providerUsername
            )
          ).map(
            item => item.toMap + ("amount" -> orderItem.amount)
          )
        )
      )
    }
  }

  override def main(args: Array[String]): Unit = {
    System.err.println("\n " + "=" * 39)
    System.err.println(s"| Server running at http://$host:$port ")

    if (args.length > 0) {
      val databaseDir = args(0)
      Database.loadDatabase(databaseDir)
      System.err.println(s"| Using database directory $databaseDir ")
    } else {
      Database.loadDatabase()  // Use default location
    }
    System.err.println(" " + "=" * 39 + "\n")

    super.main(args)
  }

  initialize()
}
