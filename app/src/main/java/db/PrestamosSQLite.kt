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
                val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
                val fechaInicio =
                    dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")))
                val fechaFin =
                    dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaFin")))
                val info = cursor.getString(cursor.getColumnIndexOrThrow("info"))

                Prestamo(idArticulo, idSocio, fechaInicio, fechaFin, info)
            } else {
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun obtenerIdPrestamoBD(prestamo: Prestamo): Int {
        val db = readableDatabase
        var prestamoId = -1

        try {
            val selectQuery = """
            SELECT idPrestamo FROM prestamos WHERE 
            (idArticulo = ? OR idArticulo IS NULL) AND 
            (idSocio = ? OR idSocio IS NULL) AND 
            (fechaInicio = ? OR fechaInicio IS NULL) AND 
            (fechaFin = ? OR fechaFin IS NULL) AND 
            (info = ? OR info IS NULL)
        """
            val parametros = arrayOf(
                prestamo.idArticulo.toString(),
                prestamo.idSocio.toString(),
                dateFormat.format(prestamo.fechaInicio),
                dateFormat.format(prestamo.fechaFin),
                prestamo.info
            )

            db.rawQuery(selectQuery, parametros).use { cursor ->
                if (cursor.moveToFirst()) {
                    prestamoId = cursor.getInt(cursor.getColumnIndexOrThrow("idPrestamo"))
                }
            }
        } catch (e: Exception) {
            Log.e("PrestamosSQLite", "Error al obtener el ID del prestamo: ${e.message}")
        } finally {
            db.close()
        }
        return prestamoId
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
        db.close()
        Log.d("PrestamosSQLite", "Prestamo insertado: ${values} con ID: $idPrestamo")
        return idPrestamo
    }

    fun actualizarPrestamo(idPrestamo: Int, prestamo: Prestamo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idArticulo", prestamo.idArticulo)
            put("idSocio", prestamo.idSocio)
            put("fechaInicio", dateFormat.format(prestamo.fechaInicio))
            put("fechaFin", dateFormat.format(prestamo.fechaFin))
            put("info", prestamo.info)
        }
        db.update("prestamos", values, "idPrestamo = ?", arrayOf(idPrestamo.toString()))
        Log.d(
            "CRUD UPDATE", "Prestamo Id: ${idPrestamo} correctamente actualizado \n${values}"
        )
        db.close()
    }

    fun borrarPrestamo(idPrestamo: Int): Int {
        val db = writableDatabase
        val rowsAffected = db.delete("prestamos", "idPrestamo = ?", arrayOf(idPrestamo.toString()))
        Log.d("CRUD DELETE", "Prestamo id: ${idPrestamo} correctamente eliminado")
        db.close()
        return rowsAffected
    }}