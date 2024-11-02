package pf.dam.socios

import java.io.Serializable
import java.util.Date

open class Socio(

    var idSocio: Int? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val numeroSocio: Int? = null,
    val telefono: Int? = null,
    val email: String? = null,
    val fechaNacimiento: Date? = null,
    val fechaIngresoSocio: Date? = null,
    val genero: Genero? = null

) : Serializable

enum class Genero {
    HOMBRE,
    MUJER
}