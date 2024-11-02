package db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import pf.dam.socios.Genero
import pf.dam.socios.Socio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.format

class SociosDbHelper(private val dbHelper: SociosSQLite) {

    fun obtenerSocios(db: SQLiteDatabase): List<Socio> {
        val listaSocios = mutableListOf<Socio>()
        db.rawQuery("SELECT * FROM socios", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    listaSocios.add(cursor.toSocio())
                } while (cursor.moveToNext())
            }
        }
        return listaSocios
    }

    fun obtenerSocioPorId(db: SQLiteDatabase, idSocio: Int): Socio? {
        return db.query(
            "socios", null, "idSocio = ?", arrayOf(idSocio.toString()),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.toSocio() else null
        }
    }

    fun insertarSocio(db: SQLiteDatabase, socio: Socio): Long {
        val values = ContentValues().apply {
            put("nombre", socio.nombre)
            put("apellido", socio.apellido)
            put("numeroSocio", socio.numeroSocio)
            put("telefono", socio.telefono)
            put("email", socio.email)
            put("fechaNacimiento", formatearFecha(socio.fechaNacimiento)) // Nuevo campo
            put("fechaIngresoSocio", formatearFecha(socio.fechaIngresoSocio)) // Nuevo campo
            put("genero", socio.genero?.name) // Nuevo campo
        }
        val idSocio = db.insert("socios", null, values)
        socio.idSocio = idSocio.toInt()
        Log.d("SociosSQLite", "Socio insertado con ID: $idSocio")
        return idSocio
    }

    fun actualizarSocio(db: SQLiteDatabase, socio: Socio) {
        val values = ContentValues().apply {
            put("nombre", socio.nombre)
            put("apellido", socio.apellido)
            put("numeroSocio", socio.numeroSocio)
            put("telefono", socio.telefono)
            put("email", socio.email)
            put("fechaNacimiento", formatearFecha(socio.fechaNacimiento)) // Nuevo campo
            put("fechaIngresoSocio", formatearFecha(socio.fechaIngresoSocio)) // Nuevo campo
            put("genero", socio.genero?.name) // Nuevo campo
        }
        db.update("socios", values, "idSocio = ?", arrayOf(socio.idSocio.toString()))
    }

    fun borrarSocio(db: SQLiteDatabase, idSocio: Int): Int {
        return db.delete("socios", "idSocio = ?", arrayOf(idSocio.toString()))
    }

    private fun Cursor.toSocio(): Socio {
        return Socio(
            getInt(getColumnIndexOrThrow("idSocio")),
            getString(getColumnIndexOrThrow("nombre")) ?: "",
            getString(getColumnIndexOrThrow("apellido")) ?: "",
            getInt(getColumnIndexOrThrow("numeroSocio")),
            getInt(getColumnIndexOrThrow("telefono")),
            getString(getColumnIndexOrThrow("email")) ?: "",
            parsearFecha(getString(getColumnIndexOrThrow("fechaNacimiento"))), // Nuevo campo
            parsearFecha(getString(getColumnIndexOrThrow("fechaIngresoSocio"))), // Nuevo campo
            obtenerGenero(getString(getColumnIndexOrThrow("genero"))) // Nuevo campo
        )
    }

    // Funciones auxiliares para formatear y parsear fechas
    private fun formatearFecha(fecha: Date?): String? {
        return if (fecha != null) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fecha)
        } else {
            null
        }
    }

    private fun parsearFecha(fechaString: String?): Date? {
        return if (fechaString != null) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fechaString)
        } else {
            null
        }
    }

    // Función auxiliar para obtener el género desde el nombre del enum
    private fun obtenerGenero(generoString: String?): Genero? {
        return if (generoString != null) {
            Genero.valueOf(generoString)
        } else {
            null
        }
    }
}