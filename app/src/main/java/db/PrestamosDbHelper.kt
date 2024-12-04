package db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.Prestamo
import java.text.SimpleDateFormat
import java.util.Locale

class PrestamosDbHelper(private val dbHelper: PrestamosSQLite) {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun getAllPrestamos(db: SQLiteDatabase): List<Prestamo> {
        val listaPrestamos = mutableListOf<Prestamo>()
        db.rawQuery("SELECT * FROM prestamos", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    listaPrestamos.add(cursor.toPrestamo())
                } while (cursor.moveToNext())
            }
        }
        return listaPrestamos
    }

    fun getPrestamoById(db: SQLiteDatabase, idPrestamo: Int): Prestamo? {
        return db.query(
            "prestamos", null, "idPrestamo = ?", arrayOf(idPrestamo.toString()),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val prestamo = cursor.toPrestamo()
                prestamo
            } else {
                null
            }
        }
    }

    fun addPrestamo(db: SQLiteDatabase, prestamo: Prestamo): Long {
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
        return idPrestamo
    }

    fun updatePrestamo(db: SQLiteDatabase, prestamo: Prestamo) {
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
            Log.d(
                "PrestamosSQLite",
                "No se pudo actualizar el préstamo con ID: ${prestamo.idPrestamo}"
            )
        }
    }

    fun deletePrestamo(db: SQLiteDatabase, idPrestamo: Int): Int {
        val affectedRows = db.delete("prestamos", "idPrestamo = ?", arrayOf(idPrestamo.toString()))
        return affectedRows
    }

    //--------------------------------------------------------------------------------------------------------------
    fun getPrestamosByArticulo(db: SQLiteDatabase, idArticulo: Int): List<Prestamo> {
        val listaPrestamos = mutableListOf<Prestamo>()
        db.rawQuery("SELECT * FROM prestamos WHERE idArticulo = ?", arrayOf(idArticulo.toString()))
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        listaPrestamos.add(cursor.toPrestamo())
                    } while (cursor.moveToNext())
                }
            }
        return listaPrestamos
    }

    fun getPrestamosBySocio(db: SQLiteDatabase, idSocio: Int): List<Prestamo> {
        val listaPrestamos = mutableListOf<Prestamo>()
        db.rawQuery("SELECT * FROM prestamos WHERE idSocio = ?", arrayOf(idSocio.toString()))
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        listaPrestamos.add(cursor.toPrestamo())
                    } while (cursor.moveToNext())
                }
            }
        return listaPrestamos
    }

    fun actualizarEstadoPrestamo(db: SQLiteDatabase, idPrestamo: Int, nuevoEstado: EstadoPrestamo) {
        val values = ContentValues().apply { put("estado", nuevoEstado.toString()) }
        db.update("prestamos", values, "idPrestamo = ?", arrayOf(idPrestamo.toString()))
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

    fun getIdSocioPrestamoActivo(db: SQLiteDatabase, idArticulo: Int): Int? {
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

    fun getUltimoPrestamoPorArticulo(db: SQLiteDatabase, idArticulo: Int): Prestamo? {
        return db.rawQuery(
            "SELECT * FROM prestamos WHERE idArticulo = ? ORDER BY fechaInicio DESC LIMIT 1",
            arrayOf(idArticulo.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.toPrestamo()
            } else {
                null
            }
        }
    }

    fun getSociosPrestamosActivos(db: SQLiteDatabase): List<Int> {
        val sociosConPrestamosActivosIds = mutableListOf<Int>()
        val cursor = db.rawQuery(
            "SELECT DISTINCT idSocio FROM prestamos WHERE estado = ?",
            arrayOf(EstadoPrestamo.ACTIVO.toString())
        )
        while (cursor.moveToNext()) {
            sociosConPrestamosActivosIds.add(cursor.getInt(cursor.getColumnIndexOrThrow("idSocio")))
        }
        cursor.close()
        return sociosConPrestamosActivosIds
    }

    fun getSociosPrestamosCerrados(db: SQLiteDatabase): List<Int> {
        val sociosConPrestamosCerradosIds = mutableListOf<Int>()
        val cursor = db.rawQuery(
            "SELECT DISTINCT idSocio FROM prestamos WHERE estado = ?",
            arrayOf(EstadoPrestamo.CERRADO.toString())
        )
        while (cursor.moveToNext()) {
            sociosConPrestamosCerradosIds.add(cursor.getInt(cursor.getColumnIndexOrThrow("idSocio")))
        }
        cursor.close()
        return sociosConPrestamosCerradosIds
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