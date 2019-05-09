## Lab 02 - Applicacion Rapy

En este lab usamos el lenguaje Scala con el framework Cask para diseñar una API
de deliveries de objetos. En el utilizamos el estilo de arquitectura API REST
para establecer el protocolo de comunicación entre los clientes y el servidor.

Al principio nos costó tiempo entender los potenciales de Scala, ya que aunque
es un lenguaje funcional, nos costó un poco diferenciar entre programacion
funcional e imperativa y cuando debiamos caer cosas para no caer en malas
practicas.

<!-- Este parrafo no se si deberia ir así porque es parte de la consigna que
usemos las cosas funcionales de scala -->

Sentimos que muchas de las funciones que hicimos podrían haberse implementado de
una manera más elegante usando muchas de las funciones muy útiles que tiene
Scala pero como no tuvimos mucho tiempo para 'jugar' con Scala las hicimos como
sabíamos que iban a funcionar.

Por ejemplo, dentro de `RestfulAPIServer` quisimos sobrecargar la funcion items
en el metodo POST para poder realizar las distintas acciones segun la cantidad
de parametros que se pasen, o si se agrega un parametro `id` por medio del
`endpoint`.
