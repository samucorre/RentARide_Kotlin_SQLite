package pf.dam.articulos

import java.io.Serializable

open class Articulo (

    val categoria: String,
    val tipo: String,
    val nombre: String,
    val descripcion: String,
    val estado : String,
    ): Serializable