# Grupo 06		
## Corrección		
	Tag o commit corregido:	lab-2
		
### Entrega y git		100,00%
	Informe	100,00%
	Commits de cada integrante	100,00%
	En tiempo y con tag correcto	100,00%
	Commits frecuentes y con nombres significativos	100,00%
### Funcionalidad		87,00%
	Se puede compilar y correr el servidor	100,00%
	Se pueden crear y recuperar usuarios	100,00%
	Se pueden crear y recuperar items	100,00%
	Se pueden crear y recuperar orders	50,00%
	Guardan los objetos luego de crearlos y actualizarlos	70,00%
### Modularización y diseño		67,50%
	Respetaron la estructura original del código	100,00%
	En los controladores sólo hay código de validación de errores, i.e. no hay lógica de los objetos	30,00%
	La mayor parte de la funcionalidad está en la clase y no en el object companion	100,00%
	Uso de métodos heredados con super, por ejemplo en toMap	100,00%
	Los usuarios actualizan su propio balance	100,00%
	Las órdenes actualizan su propio estado	0,00%
### Calidad de código		87,50%
	Buenas prácticas funcionales	100,00%
	Líneas de más de 120 caracteres	100,00%
	Estilo de código	100,00%
	Estructuras de código simples	50,00%
### Opcionales		
	Puntos estrella	
		
# Nota Final		7,987
		
		
# Comentarios		
	- En order detail, buscan el id del proveedor adentro de map que recorre los items de la Orden. Es mucho más eficiente buscarlo una única vez y guardarlo en el val.	
	- Además, en order detail han devuelto el id de la orden en lugar del id del item	
	- Al comprobar si el item existe al crear la orden, pusieron un forall y es un exists. Se entiende por qué?	
	- No actualizaron el balance de los usuarios al crear la orden	
	- En Model.find, no era necesario volver a diferenciar entre el caso Some y el None, con llamar a la función "get" sobre el Map dbtable.instances era suficiente	
	- En Model tienen muchas variables auxiliares que complican el código más que simplificarlo	
	- Aunque es poco código, se podría haber usado una clase abstracta User para métodos como el updateBalance. Además de ello, no sé si por defecto debería restarse en el consumidor y agregarse en el proveedor. Si el método se llamara "pay" sí, pero como se llama "updateBalance" también podría utilizarse para agregar crédito al consumidor.	
	- El código de get orders está demasiado complejo, sale en menos de 15 líneas. La mayoría de esa lógica debería estar en Order, no en el controller.	
	- Con respecto a los estados de las órdenes, de la forma en que lo plantearon, el controlador (ResfulAPIServer) tiene que estar al tanto de los estados posibles y de las transacciones que tienen. Es más limpio poner un método "deliver" en el objeto Order, y crear una orden directamente con el estado "payed" o algún otro por defecto. Se entiende?	
