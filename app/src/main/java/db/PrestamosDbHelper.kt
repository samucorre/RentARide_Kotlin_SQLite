package db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.compose.foundation.layout.size
import pf.dam.articulos.Articulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.Prestamo
import pf.dam.socios.Socio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrestamosDbHelper(private val dbHelper: PrestamosSQLite) {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun obtenerPrestamos(db: SQLiteDatabase): List<Prestamo> {
        val listaPrestamos = mutableListOf<Prestamo>()
        db.rawQuery("SELECT * FROM prestamos", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    listaPrestamos.add(cursor.toPrestamo())
                } while (cursor.moveToNext())
            }
        }
        Log.d("PrestamosSQLite", "Se obtuvieron ${listaPrestamos.size} préstamos")
        return listaPrestamos
    }

    fun obtenerPrestamoPorId(db: SQLiteDatabase, idPrestamo: Int): Prestamo? {
        return db.query(
            "prestamos", null, "idPrestamo = ?", arrayOf(idPrestamo.toString()),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val prestamo = cursor.toPrestamo()
                Log.d("PrestamosSQLite", "Préstamo obtenido con ID: $idPrestamo")
                prestamo
            } else {
                Log.d("PrestamosSQLite", "No se encontró préstamo con ID: $idPrestamo")
                null
            }
        }
    }

    fun insertarPrestamo(db: SQLiteDatabase, prestamo: Prestamo): Long {
        val values = ContentValues().apply {
            put("idArticulo", prestamo.idArticulo)
            put("idSocio", prestamo.idSocio)
            put("fechaInicio", dateFormat.format(prestamo.fechaInicio))
            put("fechaFin", prestamo.fechaFin?.let { dateFormat.format(it) })
            put("info", prestamo.info)
            put("estado", prestamo.estado.toString())
        }
        val idPrestamo = db.insert("prestamos", null, values)
        prestamo.idPrestamo = idPrestamo.toInt()
        Log.d("PrestamosSQLite", "Préstamo insertado con ID: $idPrestamo")
        return idPrestamo
    }

    fun actualizarPrestamo(db: SQLiteDatabase, prestamo: Prestamo) {
        val values = ContentValues().apply {
            put("idArticulo", prestamo.idArticulo)
            put("idSocio", prestamo.idSocio)
            put("fechaInicio", dateFormat.format(prestamo.fechaInicio))
            put("fechaFin", prestamo.fechaFin?.let { dateFormat.format(it) })
            put("info", prestamo.info)
            put("estado", prestamo.estado.toString())
        }
        val affectedRows = db.update(
            "prestamos", values, "idPrestamo = ?", arrayOf(prestamo.idPrestamo.toString())
        )
        if (affectedRows > 0) {
            Log.d("PrestamosSQLite", "Préstamo actualizado con ID: ${prestamo.idPrestamo}")
        } else {
            Log.d("PrestamosSQLite", "No se pudo actualizar el préstamo con ID: ${prestamo.idPrestamo}")
        }
    }

    fun borrarPrestamo(db: SQLiteDatabase, idPrestamo: Int): Int {
        val affectedRows = db.delete("prestamos", "idPrestamo = ?", arrayOf(idPrestamo.toString()))
        if (affectedRows > 0) {
            Log.d("PrestamosSQLite", "Préstamo eliminado con ID: $idPrestamo")
        } else {
            Log.d("PrestamosSQLite", "No se pudo eliminar el préstamo con ID: $idPrestamo")
        }
        return affectedRows
    }

    fun obtenerPrestamosPorArticulo(db: SQLiteDatabase, idArticulo: Int): List<Prestamo> {
        val listaPrestamos = mutableListOf<Prestamo>()
        db.rawQuery("SELECT * FROM prestamos WHERE idArticulo = ?", arrayOf(idArticulo.toString()))
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        listaPrestamos.add(cursor.toPrestamo())
                    } while (cursor.moveToNext())
                }
            }
        Log.d(
            "PrestamosSQLite",
            "Se obtuvieron ${listaPrestamos.size} préstamos para el artículo con ID: $idArticulo"
        )
        return listaPrestamos
    }

    fun obtenerPrestamosPorSocio(db: SQLiteDatabase, idSocio: Int): List<Prestamo> {
        val listaPrestamos = mutableListOf<Prestamo>()
        db.rawQuery("SELECT * FROM prestamos WHERE idSocio = ?", arrayOf(idSocio.toString()))
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        listaPrestamos.add(cursor.toPrestamo())
                    } while (cursor.moveToNext())
                }
            }
        Log.d(
            "PrestamosSQLite",
            "Se obtuvieron ${listaPrestamos.size} préstamos para el socio con ID: $idSocio"
        )
        return listaPrestamos
    }

    fun actualizarEstadoPrestamo(db: SQLiteDatabase, idPrestamo: Int, nuevoEstado: EstadoPrestamo) {
        val values = ContentValues().apply { put("estado", nuevoEstado.toString()) }
        val affectedRows =
            db.update("prestamos", values, "idPrestamo = ?", arrayOf(idPrestamo.toString()))
        if (affectedRows > 0) {
            Log.d(
                "PrestamosSQLite",
                "Estado del préstamo actualizado con ID: $idPrestamo a $nuevoEstado"
            )
        } else {
            Log.d("PrestamosSQLite", "No se pudo actualizar el estado del préstamo con ID: $idPrestamo")
        }
    }

    fun estaArticuloEnPrestamoActivo(db: SQLiteDatabase, idArticulo: Int): Boolean {
        return db.rawQuery(
            "SELECT * FROM prestamos WHERE idArticulo = ? AND estado = ?",
            arrayOf(idArticulo.toString(), EstadoPrestamo.ACTIVO.toString())
        ).use { cursor -> cursor.count > 0 }
    }

    fun estaSocioEnPrestamoActivo(db: SQLiteDatabase, idSocio: Int): Boolean {
        return db.rawQuery(
            "SELECT * FROM prestamos WHERE idSocio = ? AND estado = ?",
            arrayOf(idSocio.toString(), EstadoPrestamo.ACTIVO.toString())
        ).use { cursor -> cursor.count > 0 }
    }

    fun obtenerIdSocioPrestamoActivo(db: SQLiteDatabase, idArticulo: Int): Int? {
        return db.rawQuery(
            "SELECT idSocio FROM prestamos " +
                    "WHERE idArticulo = ? AND estado = ?",
            arrayOf(idArticulo.toString(), EstadoPrestamo.ACTIVO.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
            } else {
                null
            }
        }
    }


    @SuppressLint("Range")
    private fun Cursor.toPrestamo(): Prestamo {
        return Prestamo(
            getInt(getColumnIndexOrThrow("idPrestamo")),
            getInt(getColumnIndexOrThrow("idArticulo")),
            getInt(getColumnIndexOrThrow("idSocio")),
            dateFormat.parse(getString(getColumnIndexOrThrow("fechaInicio")))!!,
            getString(getColumnIndex("fechaFin"))?.let { dateFormat.parse(it) },
            getString(getColumnIndexOrThrow("info")) ?: "",
            EstadoPrestamo.valueOf(getString(getColumnIndexOrThrow("estado")))
        )
    }
}