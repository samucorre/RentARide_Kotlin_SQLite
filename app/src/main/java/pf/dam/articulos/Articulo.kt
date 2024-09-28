package pf.dam.articulos

import java.io.Serializable

open class Articulo (

    val categoria: String? = null,
    val tipo: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val estado : EstadoArticulo? = null,
    val rutaImagen: String? = null
    ): Serializable

enum class EstadoArticulo {
    DISPONIBLE,
    PRESTADO
}