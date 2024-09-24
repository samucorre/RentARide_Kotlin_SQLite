package db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pf.dam.prestamos.Prestamo
import java.text.SimpleDateFormat
import java.util.*

class PrestamosSQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "prestamos.db"
        private const val DATABASE_VERSION = 1
    }
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    override fun onCreate(db: SQLiteDatabase) {
        val createTablePrestamos = """
            CREATE TABLE prestamos (
                idPrestamo INTEGER PRIMARY KEY AUTOINCREMENT,
                idArticulo INTEGER,
                idSocio INTEGER,
                fechaInicio TEXT,
                fechaFin TEXT,
                info TEXT,
                FOREIGN KEY (idSocio) REFERENCES socios (idSocio),
                FOREIGN KEY (idArticulo) REFERENCES articulos (idArticulo)
            )
        """.trimIndent()
        db.execSQL(createTablePrestamos)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS prestamos")
        onCreate(db)
    }

    fun insertarPrestamo(prestamo: Prestamo): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("idArticulo", prestamo.idArticulo)
        values.put("idSocio", prestamo.idSocio)
        values.put("fechaInicio", dateFormat.format(prestamo.fechaInicio))
        values.put("fechaFin", dateFormat.format(prestamo.fechaFin))
        values.put("info", prestamo.info)


        val idPrestamo = db.insert("prestamos", null, values)
        db.close()
        Log.d("PrestamosSQLite", "Prestamo insertado con ID: $idPrestamo")
        return idPrestamo
    }

    fun obtenerPrestamos(): List<Prestamo> {
        val db = readableDatabase
        val listaPrestamos = mutableListOf<Prestamo>()

        try {
            val selectQuery = "SELECT * FROM prestamos"
            db.rawQuery(selectQuery, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                        val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
                        val fechaInicio = dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")))
                        val fechaFin = dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaFin")))
                        val info = cursor.getString(cursor.getColumnIndexOrThrow("info"))

                        val prestamo = Prestamo(
                            idArticulo,
                            idSocio,
                            fechaInicio,
                            fechaFin,
                            info
                        )
                        listaPrestamos.add(prestamo)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Error obteniendo préstamos: ${e.message}", e)
        } finally {
            db.close()
        }
        return listaPrestamos
    }

    // ... otras funciones para obtener, actualizar y eliminar préstamos
}