## Lab 02 - Applicacion Rapy

En este lab usamos el lenguaje Scala con el framework Cask para diseñar una API
de delivery de objetos. En el utilizamos el estilo de arquitectura API REST
para establecer el protocolo de comunicación entre los clientes y el servidor.

Al principio nos costó tiempo entender los potenciales de Scala, ya que aunque
es un lenguaje funcional, nos costó un poco diferenciar entre programacion
funcional e imperativa y cuando debiamos caer cosas para no caer en malas
practicas.

Además, al ir conociendo el lenguaje fuimos descubriendo funcionalidades como
`match`, `case class` y `trait` que nos permitieron ampliar nuestro conocimiento
para poder terminar este proyecto.

Tuvimos algunas dificultades con el manejo de sobrecargas en funciones que
tomaban parámetros parecidos pero tenían rutas diferentes y Scala no tomaba como
una sobrecarga a la función y levantaba un error de tipo. Solicionamos esto
cambiándole el nombre a alguna de las funciones, eliminando así la sobrecarga.
