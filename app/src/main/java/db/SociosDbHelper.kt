package db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.icu.util.Calendar
import android.util.Log

import pf.dam.socios.Genero
import pf.dam.socios.Socio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.format

class SociosDbHelper(private val dbHelper: SociosSQLite) {


    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

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
            put("fechaNacimiento",dateFormat.format(socio.fechaNacimiento))
            put("fechaIngresoSocio",dateFormat.format(socio.fechaIngresoSocio))
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
            put("fechaNacimiento", socio.fechaNacimiento?.let { dateFormat.format(it) }) // Nuevo campo
            put("fechaIngresoSocio", socio.fechaIngresoSocio?.let { dateFormat.format(it) }) // Nuevo campo
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
            dateFormat.parse(getString(getColumnIndexOrThrow("fechaNacimiento")))!!, // Nuevo campo
            dateFormat.parse(getString(getColumnIndexOrThrow("fechaIngresoSocio")))!!, // Nuevo campo
            obtenerGenero(getString(getColumnIndexOrThrow("genero"))) // Nuevo campo
        )
    }

    // Funciones auxiliares para formatear y parsear fechas
    private fun formatearFecha(fecha: Date?): String? {
        return if (fecha != null) {
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(fecha)
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

    //Función auxliar para obtener los socios que cumplen años en el mes actual
    fun obtenerSociosCumplenAnosMesActual(db: SQLiteDatabase): List<Pair<String, Date>> {
        val listaSociosCumpleaneros = mutableListOf<Pair<String, Date>>()
        val mesActual = Calendar.getInstance().get(Calendar.MONTH) + 1 // Mes actual (1-12)

        db.rawQuery("SELECT nombre, apellido, fechaNacimiento FROM socios", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val fechaNacimiento = parsearFecha(cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento")))
                    fechaNacimiento?.let {
                        val mesNacimiento = Calendar.getInstance().apply { time = it }.get(Calendar.MONTH) + 1
                        if (mesNacimiento == mesActual) {
                            val nombreCompleto = "${cursor.getString(cursor.getColumnIndexOrThrow("nombre"))} ${cursor.getString(cursor.getColumnIndexOrThrow("apellido"))} ${formatearFecha(it)}"
                            listaSociosCumpleaneros.add(Pair(nombreCompleto, it))
                        }
                    }
                } while (cursor.moveToNext())
            }
        }

        return listaSociosCumpleaneros
    }

    //Función auxiliar para obtener el último socio registrado
    fun obtenerUltimoSocioRegistrado(db: SQLiteDatabase): Socio? {
        return db.rawQuery("SELECT * FROM socios ORDER BY idSocio DESC LIMIT 1", null).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.toSocio()
            } else {
                null
            }
        }
    }
}