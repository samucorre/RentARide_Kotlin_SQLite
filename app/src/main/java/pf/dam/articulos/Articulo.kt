package pf.dam.articulos

import java.io.Serializable

open class Articulo (

    var idArticulo: Int? = null,
    val categoria: String? = null,
    val tipo: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val estado : EstadoArticulo? = null,
    val rutaImagen: String? = null,
    var softDeletedArticulo: Boolean = false

    ): Serializable

enum class EstadoArticulo {
    DISPONIBLE,
    NO_DISPONIBLE,
    PRESTADO
}