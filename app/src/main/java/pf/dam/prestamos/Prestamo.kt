package pf.dam.prestamos

import java.io.Serializable
import java.util.Date

open class Prestamo(

    var idPrestamo: Int? = null,
    val idArticulo: Int,
    val idSocio: Int,
    val fechaInicio: Date,
    val fechaFin: Date?=null,
    val info: String,
    var estado: EstadoPrestamo = EstadoPrestamo.ACTIVO
) : Serializable

enum class EstadoPrestamo {
    ACTIVO,
    CERRADO
}