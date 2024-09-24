package db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pf.dam.socios.Socio

class SociosSQLite(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "socios.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSocios = """
            CREATE TABLE socios (
                idSocio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                numeroSocio INTEGER NOT NULL,
                telefono INTEGER NOT NULL,
                email TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableSocios)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS socios")
        onCreate(db)
    }

    fun insertarSocio(socio: Socio): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("nombre", socio.nombre)
        values.put("apellido", socio.apellido)
        values.put("numeroSocio", socio.numeroSocio)
        values.put("telefono", socio.telefono)
        values.put("email", socio.email.toString()) // Asegúrate de que Email se pueda convertir a String

        val idSocio = db.insert("socios", null, values)
        db.close()
        Log.d("SociosSQLite", "Socio insertado con ID: $idSocio")
        return idSocio
    }

    fun obtenerSocios(): List<Socio> {
        val db = readableDatabase
        val listaSocios = mutableListOf<Socio>()

        try {
            val selectQuery = "SELECT * FROM socios"
            db.rawQuery(selectQuery, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                        val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                        val numeroSocio = cursor.getInt(cursor.getColumnIndexOrThrow("numeroSocio"))
                        val telefono = cursor.getInt(cursor.getColumnIndexOrThrow("telefono"))
                        val email = cursor.getString(cursor.getColumnIndexOrThrow("email")) // Asegúrate de que se pueda convertir a Email

                        val socio = Socio(
                            nombre,
                            apellido,
                            numeroSocio,
                            telefono,
                           email,
                        )
                        listaSocios.add(socio)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Error obteniendo socios: ${e.message}", e)
        } finally {
            db.close()
        }
        return listaSocios
    }
}