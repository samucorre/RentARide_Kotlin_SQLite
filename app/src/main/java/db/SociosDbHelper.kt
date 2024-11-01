package db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import pf.dam.socios.Socio

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
            getString(getColumnIndexOrThrow("email")) ?: ""
        )
    }
}