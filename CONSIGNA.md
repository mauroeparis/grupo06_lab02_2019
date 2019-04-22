Consigna
========

## Algo de contexto

Las aplicaciones web se dividen usualmente en frontend y backend, que se
corresponden a los componentes ejecutados en el cliente y en el servidor. El
backend, ejecutado en el servidor, implementa los diferentes procesos de
negocios, que usualmente involucran interactuar con alguna base de datos y con
otros servicios. Recibe requests de múltiples clientes al mismo tiempo, las
encola y eventualmente envía las respuestas correspondientes. El frontend, por
otra parte, es el encargado de la visualización de los componentes gráficos en
el browser, de enviar los pedidos (HTTP Requests) al servidor y de la conexión
entre los distintos servicios provistos por el backend.

Un patrón comúnmente utilizado para implementar aplicaciones web (y en general
para aplicaciones que tenga una interfaz de usuario) es el de
Model-View-Controller. En este patrón, los modelos (Models) son los componentes
principales y expresan los comportamientos de la aplicación, es decir
implementan las reglas de negocios y administran los datos de la misma. En
aplicaciones simples, suele existir un mapeo uno a uno entre tablas en la base
de datos y modelos. Las vistas (Views) manejan la interacción con el usuario y
determinan cómo se renderiza la información. Luego de una acción, las vistas
envían requests a los controladores. Los controladores (Controllers) son las
funciones que abstraen la lógica de la aplicación y la interacción entre los
objetos.

Si bien, históricamente los límites entre frontend y backend han estado
claramente marcados, en los últimos años el aumento en las capacidades de
procesamiento del lado del cliente (e.g. mejores engines de Javascript) ha
provocado que cada vez más funcionalidades que originalmente se implementan en
el servidor se estén implementando en el cliente. Como consecuencia, se
desarrollaron numerosos frameworks que permiten implementar mucho de la lógica
de la aplicación, como AngularJS, React o Vue.

Así como muchas funcionalidades se implementan ahora en el frontend, es muy
común el desarrollo de aplicaciones web sin un frontend definido que proveen
acceso a sus funcionalidades a través de un conjunto bien definido de
interfaces que se denomina API. Este tipo de aplicaciones, denominadas
servicios web, es muy común en la actualidad y permite la generación de nuevos
productos mediante la integración de diferentes servicios web. Por ejemplo, se
utilizan cuando una empresa es la encarga desarrollar el backend y otra el
frontend.  En estos casos, el frontend y el backend se comunican a través de
interfaces llamadas APIs, que definen un conjunto de *requests* posibles y sus
*responses* esperados.

Por ejemplo, [MercadoLibre tiene una
API](https://developers.mercadolibre.com.ar/es_ar/api-docs-es) que permite a
desarrolladores externos a la empresa interactuar con su base de datos
directamente, sin tener que utilizar la interfaz gráfica de la web. Por
ejemplo, podemos consultar todas las categorías de productos si hacemos un get
a la URL https://api.mercadolibre.com/sites/MLA/categories

    $ curl https://api.mercadolibre.com/sites/MLA/categories

## El proyecto

En este laboratorio vamos a implementar una API RESTful para un servicio de
delivery, al estilo Rappi, PedidosYA, ifood o Deliveroo. En esta API, los
proveedores suben sus productos, que los consumidores pueden ver y seleccionar
para incluir en un pedido. Una vez que el pedido está finalizado, la aplicación
se encarga de cobrarlo al consumidor, entregarlo y pagarle al proveedor el
dinero correspondiente.

Se les proveerá de un esqueleto inicial con código base sobre el cuál construir
su aplicatión y la especificación de la API (i.e. la especificación de cada
"endpoint").

El backend estará implementado en [Cask](http://www.lihaoyi.com/cask/), un
microframework web del lenguaje de programación
[Scala](https://www.scala-lang.org/), de diseño simple inspirado en el
framework web [Flask](http://flask.pocoo.org/) de Python.

Tendrán que completar el esqueleto implementando las funciones que procesan las
peticiones HTTP (GET o POST) que vienen del frontend, y tendrán que definir
modelos de datos (i.e. tipos de datos) utilizando programación orientada a
objetos.

No contaremos con un frontend, por lo que todas las consultas deberán ser
realizadas a través de un navegador (e.g. utilizando `postman`) o por consola
con herramientas como `curl` o [`httpie`](https://httpie.org/).

### Algunas simplificaciones

Para mantener simple nuestro prototipo, tomaremos algunas decisiones de diseño
que no son óptimas si estuviéramos implementando un producto real:

- La "base de datos" estará manejada mediante un
  [singleton](https://en.wikipedia.org/wiki/Singleton_pattern), mientras que
  las "tablas" serán instancias de una clase. Casi toda la implementación de la
  base de datos será dada, simplemente tienen que adaptarla a su diseño de
  objetos.
- Los datos serán guardados como archivos [JSON](https://www.json.org/). Esto
  es, guardaremos los objetos (usuarios registrados, ubicaciones, pedidos y
  productos) en formato JSON. De esta forma, no hay que preocuparse por
  configurar un motor de base de datos.
- En lugar de usar alguna interfaz a google maps, las ubicaciones serán simples
  strings predefinidos con nombres como “Parque Sarmiento” o “Alta Córdoba” y
  coordenadas.
- No es necesario manejar los casos de uso con errores "elegantemente" (e.g.,
  verificación de direcciones de email con formato real). Sin embargo, es
  necesario comprobar que todos los elementos usados una consulta existan en la
  base de datos.
- No se pedirán contraseñas para los usuarios (sólo como punto estrella).
- La aplicación no requerirá login/logout, eso quedará como punto estrella.
- A la hora de listar los proveedores para un consumidor en particular,
  listaremos solamente aquellos que tengan la misma dirección. El campo de
  máxima distancia se utilizará sólo para los puntos estrellas.
- Dejaremos que cask maneje los errores originados cuando una consulta no tiene
  suficientes argumentos. Esto devolverá un error 500 ante una consulta mal
  formada, en lugar de un error 400.

## Requerimientos Funcionales

1. Registro de usuarios. Existen dos tipos de usuarios: consumidores
   (consumers) y proveedores (providers) y un usuario solo puede ser de un
   tipo. Un consumidor está definido por username (probablemente el email), una
   dirección y una contraseña. Por su parte, un proveedor está definido por un
   username, una dirección, una contraseña, un nombre del negocio y una
   distancia máxima de delivery. El username define univocamente tanto a los
   consumidores como a los proveedores, y no pueden existir consumidores y
   proveedores con el mismo username. De igual manera, el nombre de la tienda
   de un proveedor debe ser unívoco entre proveedores.
2. Los consumidores y proveedores comenzarán con un balance de 0 que sólo será
   modificado cuando se complete un pedido. El total del pedido será restado al
   saldo del consumidor (quien manejará saldo negativo) y sumado al del
   proveedor.
3. A partir de una dirección obtener un listado de proveedores que hagan
   delivery hasta esa dirección particular.
4. Crear un pedido (order) a un único proveedor. Este pedido puede contener
   múltiples items. Cuando la orden es creada, se debe actualizar el saldo del
   consumidor, proveedor y el estado del pedido.
5. Consultar el listado de pedidos realizados por un consumidor en particular.
6. Crear y modificar un menú compuesto de ítems. Cada ítem debe tener una
   descripción y un precio.
7. Consultar el listado de pedidos recibidos por un proveedor y sus respectivos
   estados.
8. Marcar un pedido como entregado. [Esta acción cambia el estado del pedido a
   ‘delivered’].
9. Consultar la información de un usuario: username, dirección, balance, etc.

## Implementación

Por sobre todo, tengan en cuenta que se evaluará el diseño de las clases y
singletons, la utilización de conceptos como herencia, encapsulamiento,
polimorfismo o sobrecarga y el uso adecuado de métodos.

La interfaz de base de datos que se les dará se encargará de la mayoría de las
operaciones de lectura o escritura a disco. Sin embargo, ustedes deberán
implementar formas de acceder a los datos en las estructuras de sus modelos
(e.g. métodos para filtrar elementos, verificar que elementos existan, etc.).
Para esto se les da una lista de métodos que tendrán que implementar ustedes.

### Definición de la API


| Method | URL                          | Params                                                                                      | Code - Response                                                                                                                                                                                                                    |
|--------|------------------------------|---------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GET    | /api/locations               |                                                                                             | 200 - [{name: string, coordX: int, coordY: int}]                                                                                                                                                                                   |
| POST   | /api/locations               | {name: string, coordX: int, coordY: int }                                                   | 200 - id ; 409 - existing location name                                                                                                                                                                                            |
| GET    | /api/consumers               |                                                                                             | 200 - [{id: int, username: string, locationId: int}]                                                                                                                                                                               |
| POST   | /api/consumers               | {username: string, locationName: string}                                                    | 200 - id ; 404 - non existing location ; 409 - existing username                                                                                                                                                                   |
| GET    | /api/providers               | {locationName?: string}                                                                     | 200 - [{id: int, username: string, locationId: int, storeName: string, maxDeliveryDistance: int}] ; 404 - non existing location                                                                                                    |
| POST   | /api/providers               | {username: string, storeName: string, locationName: string, maxDeliveryDistance: int}       | 200 - id ; 400 - negative maxDeliveryDistance ; 404 - non existing location ; 409 - existing username/storeName                                                                                                                    |
| POST   | /api/users/delete/{id:int}   |                                                                                             | 200 "Ok" ; 404 - non existing user                                                                                                                                                                                                 |
| GET    | /api/items                   | {providerUsername?: string}                                                                 | 200 - [{id: int, name: string, price: float, description: string, providerId: int}*] ; 404 - non existing provider                                                                                                                 |
| POST   | /api/items                   | {name: string, description: string, price: float, providerUsername: string}                 | 200 - id ; 400 - negative price ; 404 - non existing provider ; 409 - existing item for provider                                                                                                                                   |
| POST   | /api/items/delete/{id:int}   |                                                                                             | 200 "Ok" ; 404 - non existing item                                                                                                                                                                                                 |
| GET    | /api/orders                  | {username: string}                                                                          | 200 - [{id: int, consumerId: int, consumerUsername: string, consumerLocation: string, providerId: int, providerStoreName: string, orderTotal: float, status: option(‘payed’, ‘delivered’, ‘finished’)}*] ; 404 - non existing user |
| GET    | /api/orders/detail/{id:int}  |                                                                                             | 200 - [{id: int, name: string, description: string, price: float, amount: int}] ; 404 - non existing order                                                                                                                         |
| POST   | /api/orders                  | {providerUsername: string, consumerUsername: string, items: [{name: string, amount: int}+]} | 200 - id ; 400 - negative amount ; 404 - non existing consumer/provider/item for provider                                                                                                                                          |
| POST   | /api/orders/delete/{id:int}  |                                                                                             | 200 - "Ok" ; 404 - non existing order                                                                                                                                                                                              |
| POST   | /api/orders/deliver/{id:int} |                                                                                             | 200 - "Ok" ; 404 - non existing order                                                                                                                                                                                              |
| POST   | /api/login**                 | {username: string, password: string}                                                        | 200 - {id: int, isProvider: bool} ; 401 - non existing user ; 403 - incorrect password                                                                                                                                             |
| POST   | /api/logout**                |                                                                                             | 200 - "Ok"                                                                                                                                                                                                                         |

\* El estado finished es para el punto estrella de comentarios.
\*\* Punto estrella de autenticación.

## Esqueleto del laboratorio

El laboratorio les provee un esqueleto base que tendrán que completar:

    .
    ├── build.sbt
    ├── project
    │   ├── build.properties
    │   └── Dependencies.scala
    └── src
        └── main
            └── scala
                ├── app
                │   ├── package.scala
                │   └── RestfulAPIServer.scala
                └── models
                    ├── db
                    │   ├── Database.scala
                    │   └── DatabaseTable.scala
                    ├── Location.scala
                    ├── Model.scala
                    └── package.scala

### Archivos del esqueleto

1. `build.sbt`: Archivo de configuración de [SBT](https://www.scala-sbt.org/).
   Aquí pueden agregar librerías. Tendrán que leer la documentación de SBT para
   saber como agregar nuevas librerías. Fuera de eso, todas las librerías
   necesarias ya están agregadas, sólo úsenlo en casos de puntos estrella.
2. `project/`: Ignoren este directorio. Es necesario para el correcto
   funcionamiento de SBT, pero no deberían tocarlo salvo excepciones muy
   puntuales con librerías.
3. `app/package.scala` y `models/package.scala`: Estos archivos definen los
   "package objects", que es una manera elegante en Scala de declarar
   constantes, variables o métodos de utilidad que sean globales a todo el
   paquete. No hagan abuso de su uso, pero puede que necesiten utilizarlo.
4. `app/RestfulAPIServer.scala`: Aquí se definen los controladores (API
   Endpoints) de la aplicación REST.
5. `models/db/Database.scala`: Interfaz con la "base de datos". Es un
   `singleton` que se encarga de las operaciones de lectura/escritura a disco.
   La lógica no tienen que tocarla, pero deberán agregar entradas a las tablas
   de sus modelos (ver `Location` como ejemplo).
6. `models/db/DatabaseTable.scala`: Clase para las tablas de la base de datos.
   No es necesario modificarla (si lean y entiendan lo que hace).
7. `models/Model.scala`: Trait base de sus modelos (clases). Y trait base de
   sus [companion
   objects](https://docs.scala-lang.org/tour/singleton-objects.html). Vean la
   implementación de `models/Location.scala` para entender mejor. En particular

Regla general, si un método, variable, constante, etc. está definido por `???`,
es que tienen que implementarlo ustedes.  Además de eso, el esqueleto no es
exhaustivo, puede que requieran más implementaciones (e.g. en
`ModelCompanion`).

## Requerimientos No funcionales

Además de los requerimientos funcionales, el laboratorio deberá cumplir los
siguientes requerimientos no funcionales:

### Implementación:

- No tiene que fallar con un error 500. Los 400 son aceptables, mientras
  que sean intencionales (e.g. los dados en la definición de la API).
- Deben respetar el encapsulamiento de *TODOS* los atributos y métodos.
  Recuerden que hay distintos niveles de encapsulamiento. Usen el que crean
  conveniente. Si dan permisos de escritura/lectura o hacen público un método,
  deben poder justificar por qué eso era necesario.
- Prefieran el uso de `val` en lugar de `var`. Traten de hacer las cosas
  inmutables cuando sea posible y eviten caer en prácticas de programación
  imperativa (i.e. eviten el uso de estados internos cuánto puedan). Scala es
  un lenguaje principalmente funcional y deben poder utilizarlo como tal.
- El sistema de inferencia de tipos de Scala es muy bueno y en la mayoría de
  los casos va a poder descifrar qué es lo que están declarando. Aún así es
  recomendable (y en cierto punto obligatorio) que declaren los tipos
  explícitamente, especialmente en funciones y/o métodos, y en atributos.
  Pueden obviarlo para variables de uso interno o cuando la definición del
  tipo de dato vuelva el código muy complejo.
- Aprendan a utilizar las herramientas que les brinda Scala. Sobre todo a la
  hora de trabajar con colecciones (listas, diccionarios, conjuntos, etc). No
  recurran a programación imperativa cuando las cosas se pueden resolver de
  manera funcional, utilizando expresiones de programación de alto orden (e.g.
  hagan uso de `map`, `filter`, `fold`, etc.)
- Los puntos estrella son extras. No necesitan hacerse para aprobar el lab. Si
  entregan un laboratorio con puntos estrella implementados y no está el
  laboratorio base con todas sus funcionalidades se les considerará mal. De
  esto se desprende que no deben utilizar ninguna librería extra que les
  facilite el trabajo de implementación de los modelos (funciones como `all`,
  `filter`, etc.). Deben implementarlas manualmente para aprobar el
  laboratorio, y eventualmente reimplementarlas en un branch aparte de un punto
  estrella luego de que hayan terminado la implementación base.

### Estilo:

- El estilo de código es válido si el código es legible y está prolijo. Traten
  de no pasar de las 80 columnas, y jamás sobrepasen las 120.
- Hagan buen uso de espacios e indentaciones. Nunca utilicen tabs, siempre
  prefieran espacios. Scala suele indentarse con un espacio de `2` como base.
- Todos los archivos deben tener estilo consistente.
- El objetivo de clases, atributos y el output de métodos deben estar
  documentados en inglés. No exageren tampoco, **good code is the best
  documentation**.
- Deben respetar la estructura original del proyecto, agregando nuevos archivos
  en los directorios correspondientes.
- Por sobre todas las cosas, siempre recuerden
  [KISS](https://en.wikipedia.org/wiki/KISS_principle)

### Entrega:

- Fecha de entrega: hasta el **10/05/2018** a las 23:59:59.999


Deberán crear un tag indicando el release para corregir.

    git tag -a lab-2 -m "Entrega Laboratorio 2"
    git push origin lab-2

**Si no está el tag no se corrige**. Tampoco se consideran commits posteriores
al tag.

En caso de agregar puntos estrella, deben hacer dos tags, uno con la entrega
y otro (o varios) con los puntos estrella. Esto nos permite evaluar la versión
básica en caso de que tengan un error en las modificaciones posteriores.

### Informe:

Junto con el código, deberán presentar un informe en un archivo INFORME.md que incluya:

- Decisiones de diseño relevantes. Por ejemplo, si algún punto de la consigna
  les pareció ambigüo, reporten qué interpretación siguienron.
- Puntos estrella que hayan realizado y cómo diseñaron la solución.
- Si utilizaron alguno de los siguientes conceptos en el proyecto (en su código
  o en el esqueleto ya dado) y qué habrían tenido que hacer si esta
  característica no estuviera disponible en el lenguaje. Un párrafo por
  concepto es suficiente.
    1. Encapsulamiento
    2. Herencia, clases abstractas y traits.
    3. Sobrecarga de operadores
    4. Polimorfismo

## Recomendaciones y algunos links de utilidad

- ¡Busquen en Google antes de implementar!
- ¡Comprueben después de implementar cada función, no quieran escribir todo y
  probar al final!
- Primero implementen los "casos de éxito" con código 200 de todos los
  endpoints, luego implementen los errores.
- La consola de `sbt` es su amiga. Úsenla para cargar lo que vayan haciendo.
- Saber que tipos toma y devuelve cada función/método es una gran parte del
  trabajo.
- Si la máquina lo soporta, les recomendamos usar IntelliJ Idea como IDE, con
  el plugin de scala. Este IDE facilita la escritura de código y realiza
  algunas comprobacionesde consistencia.


Algunos links de interés:

- [From Python to Scala](https://crscardellino.github.io/archive/): Guía
  escrita por el profesor Cristian Cardellino. Es una iniciación a Scala desde
  Python. No está completa ni es exhaustiva (y puede estar un poco
  desactualizada), pero cubre con lo básico para comenzar. En el link está el
  archivo del blog de Cristian, y de ahi pueden acceder a todas las entradas
  (año 2014). Disclaimer, está en inglés, pero le pueden preguntar a Cristian
  ante cualquier duda.
- [Documentación Oficial de Scala](https://docs.scala-lang.org/): Es muy buena
  y es la referencia sobre la que siempre se tienen que basar. Cualquier
  consulta sobre la [API de Scala](https://docs.scala-lang.org/api/all.html)
  puede ser resuelta en este lugar. Pero además se ofrecen varios aspectos más
  básicos como el [Tour of
  Scala](https://docs.scala-lang.org/tour/tour-of-scala.html): que cubre más
  que suficiente todos lo necesario que van a tener que utilizar en el
  laboratorio.
- [The Neophyte's Guide to
  Scala](https://danielwestheide.com/scala/neophytes.html): es una guía
  avanzada de Scala, pero sumamente recomendable si les interesa saber más del
  lenguaje.  Está ligeramente desactualizada, pero trabaja sobre conceptos
  fundamentales del lenguaje que no van a cambiar por más que las versiones
  cambien.
- [Documentación de Cask](http://www.lihaoyi.com/cask/): Es la documentación
  base del framework que utilizarán. Es escasa, pero no es muy complejo lo que
  tienen que hacer tampoco. No duden en consultar a los profesores sobre cómo
  hacer cualquier cosa.
- [Sobre métodos GET y
  POST](http://blog.micayael.com/2011/02/09/metodos-get-vs-post-del-http/):
  Para leer y entender un poco más sobre los conceptos que vienen detrás de las
  REST APIs.
- [Tips para el desarrollo utilizando
  POO](https://scotch.io/bar-talk/s-o-l-i-d-the-first-five-principles-of-object-oriented-design)


## Puntos estrella

Para implementar puntos estrella se puede cambiar cualquier parte de la
implementación. Es posible que deban modificar la especificación de la API.
Dichos cambios deberán ser documentados en el README, junto con una
justificación de por qué así lo decidieron y cómo efectivamente comprobar que
el punto estrella funciona.

Los puntos estrella deberán ser entregados en branchs aparte que deberán ser
correctamente taggeados. Acá va un listado de los puntos estrella posibles (son
bienvenidos de hacer más cosas si así lo desean). Los puntos 3 y 4 son extra
difíciles, por lo que suman mayor cantidad de puntos:

### 1- Comentarios

Proveer las funcionalidades para que los consumidores puedan puntuar y dejar
comentarios de texto sobre la comida. Esta acción marca el pedido con el estado
`finished`. Los consumidores serán capaces de ver el rating de los proveedores y
listar todos los comentarios anteriores. Los proveedores podrán acceder a esta
información desde su página de perfil.

### 2- Radio de entrega

Al listar los proveedores, mostrar sólo aquellos que hagan deliveries a la
dirección del usuario, independientemente de que estén en la misma dirección o
no.  Hint: utilizar el atributo de distancia máxima de delivery y el sistema de
coordenadas. Para calcular la distancia deberán hacer uso de [taxicab
geometry](https://en.wikipedia.org/wiki/Taxicab_geometry).

### 3- Full authentication

Deberán utilizar autenticación. Para ello el usuario deberá hacer `login` y
`logout` de la aplicación mediante el uso de [JSON Web
Tokens](https://jwt.io/). Pueden utilizar la librería que deseen para esta
aplicación. Por otro lado, las contraseñas deberán estar correctamente
encriptadas, utilizando alguna librería de hash. En particular, se deberá
pedir autorización para hacer las operaciones de escritura (los métodos POST).

### 4- Bases de datos reales

Utilizar algún motor real de base de datos (puede ser bien con motores
complejos como MySQL o PostgreSQL, o bien con SQLite). Para ello deberán
reimplementar la interfaz `Database` de manera tal que sea un
[ORM](https://en.wikipedia.org/wiki/Object-relational_mapping), o bien utilizar
algún tipo de librería que se encargue de eso.
