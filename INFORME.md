# Lab 02 - Applicacion Rapy

## Decisiones de diseño

En este lab usamos el lenguaje Scala con el framework Cask para diseñar una API
de delivery de objetos. En el utilizamos el estilo de arquitectura API REST
para establecer el protocolo de comunicación entre los clientes y el servidor.

Al principio tuvimos dificultades para entender los potenciales de Scala ya que,
aunque es un lenguaje funcional, nos costó un poco diferenciar entre
programación funcional e imperativa y que métodos deberíamos usar para no caer
en malas prácticas.

Además, al ir conociendo el lenguaje fuimos descubriendo funcionalidades como
`match`, `case class` y `trait` que nos permitieron ampliar nuestro conocimiento
para poder terminar este proyecto.

Extendimos las clases `Provider` y `Consumer` agregando una nueva función
`updateBalance` que toma el monto total de la orden y modifica el balance de
cada uno. Este monto lo conseguimos con la función `getTotal` en la clase
`Order`.

## Puntos estrella

No realizamos puntos estrellas.

## Lista de conceptos

### - Encapsulamiento

Usamos este concepto en `RestfulAPIServer` obteniendo de la interfaz las
funciones que provee `Model` para brindarles a `Consumer`, `Provider`, `Items`,
etc, métodos para manipular y manejar sus instancias.

### - Herencia, clases abstractas y traits.

LAS CLASES HEREDAN TODAS DE MODEL.scala !!!

### - Sobrecarga de operadores

Tuvimos algunas dificultades con el manejo de sobrecargas en funciones que
tomaban parámetros parecidos pero tenían rutas diferentes ya que Scala no tomaba
como una sobrecarga a la función y levantaba un error de tipo. Solucionamos esto
cambiándole el nombre a alguna de las funciones y eliminando así la sobrecarga.

El problema estaba en el wrapper `@post("/api/items/delete/id")` que también
recibía un solo argumento pero este era un `Int` y Cask la confundía con el
`@get("/api/items")` que toma un `providerUsername` que es de tipo `String` y
daba error por no poder castear este ultimo a `Int`.

### - Polimorfismo

Utilizamos polimorfismo para implementar la función `exists`. Si esta
característica no estuviera disponible podríamos haber utilizado sobrecarga o
haber aplicado lo mismo que hicimos en la función `filter`, es decir, tomar un
`Map`, filtrarlo y chequear si la lista que devuelve es o no vacía.
