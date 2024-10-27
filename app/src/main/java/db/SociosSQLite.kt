
package db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pf.dam.prestamos.Prestamo
import pf.dam.socios.Socio

class SociosSQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "socios.db"
        private const val DATABASE_VERSION = 1
    }
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var prestamosDbHelper: PrestamosSQLite



    override fun onCreate(db: SQLiteDatabase) {
        val crearTablaSocios = """
            CREATE TABLE socios (
                idSocio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                numeroSocio INTEGER,
                telefono INTEGER,
                email TEXT
            )
        """.trimIndent()
        db.execSQL(crearTablaSocios)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS socios")
        onCreate(db)
    }

    fun obtenerSocios(): List<Socio> {
        val db = readableDatabase
        val listaSocios = mutableListOf<Socio>()

        try {
            val selectQuery = "SELECT * FROM socios"
            db.rawQuery(selectQuery, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
                        val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                        val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                        val numeroSocio = cursor.getInt(cursor.getColumnIndexOrThrow("numeroSocio"))
                        val telefono = cursor.getInt(cursor.getColumnIndexOrThrow("telefono"))
                        val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))

                        val socio = Socio(
                            idSocio,
                            nombre,
                            apellido,
                            numeroSocio,
                            telefono,
                            email
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

    fun obtenerSocioPorId(idSocio: Int): Socio? {
        val db = readableDatabase
        val cursor = db.query(
            "socios",
            null,
            "idSocio = ?",
            arrayOf(idSocio.toString()),
            null,
            null,
            null
        )
        try {
            return if (cursor.moveToFirst()) {
                val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                val numeroSocio = cursor.getInt(cursor.getColumnIndexOrThrow("numeroSocio"))
                val telefono = cursor.getInt(cursor.getColumnIndexOrThrow("telefono"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))

                Socio(idSocio, nombre, apellido, numeroSocio, telefono, email)
            } else {
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun insertarSocio(socio: Socio): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", socio.nombre)
            put("apellido", socio.apellido)
            put("numeroSocio", socio.numeroSocio)
            put("telefono", socio.telefono)
            put("email", socio.email)
        }

        val idSocio = db.insert("socios", null, values)
        socio.idSocio = idSocio.toInt()
        db.close()
        Log.d("SociosSQLite", "Socio insertado con ID: $idSocio")
        return idSocio
    }

    fun actualizarSocio(socio: Socio) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", socio.nombre)
            put("apellido", socio.apellido)
            put("numeroSocio", socio.numeroSocio)
            put("telefono", socio.telefono)
            put("email", socio.email)
        }

        db.update("socios", values, "idSocio = ?", arrayOf(socio.idSocio.toString()))
        db.close()
    }

    fun borrarSocio(idSocio: Int): Int {
        val db = writableDatabase
        val rowsAffected = db.delete("socios", "idSocio = ?", arrayOf(idSocio.toString()))
        db.close()
        return rowsAffected
    }
}