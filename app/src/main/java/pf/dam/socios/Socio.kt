package pf.dam.socios

import android.provider.ContactsContract.CommonDataKinds.Email

open class Socio (
    val nombre: String,
    val apellido: String,
    val numeroSocio: Int,
    val telefono: Int,
    val email: String,
)