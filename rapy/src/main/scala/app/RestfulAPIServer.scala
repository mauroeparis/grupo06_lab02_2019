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
    if (Consumer.exists("username", username)) {
      return JSONResponse("Existing consumer", 409)
    } else if (!Location.exists("name", locationName)) {
      return JSONResponse("Nonexisting location", 404)
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
    case locationName => JSONResponse(
      Provider.filter(
        Map("locationName" -> locationName)
      )
    )
  }

  @postJson("/api/providers")
  def providers(username: String, storeName: String, locationName: String,
                maxDeliveryDistance: Int): Response = {
    if (Provider.exists("username", username)) {
      return JSONResponse("Existing username", 409)
    } else if (maxDeliveryDistance < 0) {
      return JSONResponse("negative maxDeliveryDistance", 400)
    } else if (Provider.exists("storeName", storeName)) {
      return JSONResponse("Existing storeName", 409)
    } else if (!Location.exists("name", locationName)) {
      return JSONResponse("Nonexisting location", 404)
    }

    val provider = Provider(username, storeName, locationName, maxDeliveryDistance)
    provider.save()
    JSONResponse(provider.id)
  }

  @get("/api/items")
  def items(providerUsername: String = ""): Response = providerUsername match {
    case "" => JSONResponse(
        Item.all.map(item => item.toMap)
    )
    case providerUsername => JSONResponse(
      Item.filter(Map("providerUsername" -> providerUsername))
    )
  }

  // TODO: FALTA VER CASOS DE FUNCION
  @postJson("/api/items")
  def items(name: String, description: String, price: Float,
            providerUsername: String): Response = {
      if (price < 0) {
        return JSONResponse("negative price", 400)
        // TODO: revisar linea de abajo, esta bien la sintaxis?
      } else if (!Provider.exists("username", providerUsername)) {
        return JSONResponse("non existing provider", 404)
      }

    val item = Item(name, description, price, providerUsername)
    item.save()
    JSONResponse(item.id)
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
