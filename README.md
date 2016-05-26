# final-project-programming-languages
Proyecto final de lenguajes de programación

Replicación del proyecto FUNICO creado por Camilo Cubides:

http://www.docentes.unal.edu.co/eccubidesg/docs/Publicaciones/Tesis%20de%20Maestria%20Camilo%20Cubides.pdf

Using Hybrid Adaptative Evolutionary Algorithm por Jonatan Gómez:

http://dis.unal.edu.co/~jgomezpe/docs/papers/gecco2004haea.pdf

Librería disponible en:

https://github.com/jgomezpe/unalcol

# ¿Cómo correrlo?

Solo es cargar las librerías de la carpeta lib del proyecto y correr la clase Main que se encuentra en el
la carpeta src.

Si queremos probar distintas funciones, podemos cambiar en el código del proyecto, la línea 42 (clase Main),
donde se encuentra comentado con las funciones que podemos probar el algoritmo

# ¿Problemas actuales?

* Los operadores por ahora no han sido lo suficientemente buenos para poder recorrer de una buena manera
el espacio de búsqueda entre los árboles sintácticos, cada uno de los operadores que hemos implementado
se encuentran en los paquetes mutation y xover del paquete funico.
* Dentro de la reparación de los árboles cuando aplicamos algún operado, hay ciertos casos que no se han
podido arreglar, ha sido bastante complicado de arreglar ya que aparece en casos muy específicos.
* Intentamos dar soporte a listas, y este está casi completo pero hay una parte donde nos estamos quedando
en un ciclo infinito y no hemos dado con el problema, por eso no tenemos casos de ejemplo con listas.

# Repositorio

https://github.com/JeffersonH44/final-project-programming-languages