package pf.dam.socios

import java.io.Serializable

open class Socio (

    var idSocio: Int? = null, // Nueva propiedad id
    val nombre: String,
    val apellido: String,
    val numeroSocio: Int,
    val telefono: Int,
    val email: String,

): Serializable