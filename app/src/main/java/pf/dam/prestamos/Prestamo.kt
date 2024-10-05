package pf.dam.prestamos

import java.io.Serializable
import java.util.Date

open class Prestamo(
    val idArticulo: Int,
    val idSocio: Int,
    val fechaInicio: Date,
    val fechaFin: Date,
    val info: String,

    ) : Serializable