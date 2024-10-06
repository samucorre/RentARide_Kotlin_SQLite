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
        private const val TAG = "PrestamosSQLite"
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
        Log.d(TAG, "Tabla prestamos creada")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS prestamos")
        Log.d(TAG, "Tabla prestamos eliminada")
        onCreate(db)
    }

    fun obtenerPrestamos(): List<Prestamo> {
        val db = readableDatabase
        val listaPrestamos = mutableListOf<Prestamo>()

        try {
            val selectQuery = "SELECT * FROM prestamos"
            db.rawQuery(selectQuery, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idPrestamo = cursor.getInt(cursor.getColumnIndexOrThrow("idPrestamo"))
                        val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                        val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
                        val fechaInicio = dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")))
                        val fechaFin = dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaFin")))
                        val info = cursor.getString(cursor.getColumnIndexOrThrow("info"))

                        val prestamo = Prestamo(
                            idPrestamo,
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
            Log.d(TAG, "Se obtuvieron ${listaPrestamos.size} préstamos")
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener préstamos: ${e.message}")
            throw RuntimeException("Error obteniendo préstamos: ${e.message}", e)
        } finally {
            db.close()
        }
        return listaPrestamos
    }

    fun obtenerPrestamoPorId(idPrestamo: Int): Prestamo? {
        val db = readableDatabase
        val cursor = db.query(
            "prestamos",
            null,
            "idPrestamo = ?",
            arrayOf(idPrestamo.toString()),
            null,
            null,
            null
        )
        try {
            return if (cursor.moveToFirst()) {
                val idPrestamo = cursor.getInt(cursor.getColumnIndexOrThrow("idPrestamo"))
                val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
                val fechaInicio =
                    dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")))
                val fechaFin =
                    dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaFin")))
                val info = cursor.getString(cursor.getColumnIndexOrThrow("info"))

                val prestamo = Prestamo(idPrestamo, idArticulo, idSocio, fechaInicio, fechaFin, info)
                Log.d(TAG, "Préstamo obtenido con ID: $idPrestamo")
                prestamo
            } else {
                Log.d(TAG, "No se encontró préstamo con ID: $idPrestamo")
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun insertarPrestamo(prestamo: Prestamo): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idArticulo", prestamo.idArticulo)
            put("idSocio", prestamo.idSocio)
            put("fechaInicio", dateFormat.format(prestamo.fechaInicio))
            put("fechaFin", dateFormat.format(prestamo.fechaFin))
            put("info", prestamo.info)
        }

        val idPrestamo = db.insert("prestamos", null, values)
        prestamo.idPrestamo = idPrestamo.toInt()
        db.close()
        Log.d(TAG, "Préstamo insertado con ID: $idPrestamo")
        return idPrestamo
    }

    fun actualizarPrestamo(prestamo: Prestamo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idArticulo", prestamo.idArticulo)
            put("idSocio", prestamo.idSocio)
            put("fechaInicio", dateFormat.format(prestamo.fechaInicio))
            put("fechaFin", dateFormat.format(prestamo.fechaFin))
            put("info", prestamo.info)
        }

        val affectedRows = db.update("prestamos", values, "idPrestamo = ?", arrayOf(prestamo.idPrestamo.toString()))
        db.close()
        if (affectedRows > 0) {
            Log.d(TAG, "Préstamo actualizado con ID: ${prestamo.idPrestamo}")
        } else {
            Log.d(TAG, "No se pudo actualizar el préstamo con ID: ${prestamo.idPrestamo}")
        }
    }

    fun borrarPrestamo(idPrestamo: Int): Int {
        val db = writableDatabase
        val affectedRows = db.delete("prestamos", "idPrestamo = ?", arrayOf(idPrestamo.toString()))
        db.close()
        if (affectedRows > 0) {
            Log.d(TAG, "Préstamo eliminado con ID: $idPrestamo")
        } else {
            Log.d(TAG, "No se pudo eliminar el préstamo con ID: $idPrestamo")
        }
        return affectedRows
    }
}