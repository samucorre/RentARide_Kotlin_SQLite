package pf.dam.socios

import java.io.Serializable

open class Socio (
    val nombre: String,
    val apellido: String,
    val numeroSocio: Int,
    val telefono: Int,
    val email: String,

): Serializable