package db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.compose.foundation.layout.size
import pf.dam.articulos.Articulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.Prestamo
import pf.dam.socios.Socio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrestamosSQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "prestamos.db"
        private const val DATABASE_VERSION = 2 // Incrementa la versión de la base de datos
        private const val TAG = "PrestamosSQLite"
    }

    private lateinit var articulosDbHelper: ArticulosSQLite // Declara la propiedad sin inicializarla
    private lateinit var sociosDbHelper: SociosSQLite // Declara la propiedad sin inicializarla

    init { // Inicializa las propiedades en el bloque init
        articulosDbHelper =
            ArticulosSQLite(context.applicationContext) // Usa el contexto de la aplicación
        sociosDbHelper =
            SociosSQLite(context.applicationContext) // Usa el contexto de la aplicación
    }


    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    override fun onCreate(db: SQLiteDatabase) {
        val crearTablaPrestamos = """
            CREATE TABLE prestamos (
                idPrestamo INTEGER PRIMARY KEY AUTOINCREMENT,
                idArticulo INTEGER,
                idSocio INTEGER,
                fechaInicio TEXT,
                fechaFin TEXT,
                info TEXT,
                estado TEXT NOT NULL,
                FOREIGN KEY (idSocio) REFERENCES socios (idSocio)ON DELETE CASCADE,
                FOREIGN KEY (idArticulo) REFERENCES articulos (idArticulo)ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(crearTablaPrestamos)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) { // Verifica si la versión anterior es menor que 2
            db.execSQL("DROP TABLE IF EXISTS prestamos")
            Log.d(TAG, "Tabla prestamos eliminada")
            onCreate(db)
        }
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
                        val fechaInicio =
                            dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")))
                        val fechaFinString =
                            cursor.getString(cursor.getColumnIndexOrThrow("fechaFin"))
                        val fechaFinDate: Date? = if (fechaFinString != null) {
                            dateFormat.parse(fechaFinString)
                        } else {
                            null
                        }
                        val info = cursor.getString(cursor.getColumnIndexOrThrow("info"))
                        val estado =
                            EstadoPrestamo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))

                        val prestamo = Prestamo(
                            idPrestamo,
                            idArticulo,
                            idSocio,
                            fechaInicio,
                            fechaFin = fechaFinDate,
                            info,
                            estado
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
                val fechaFinString = cursor.getString(cursor.getColumnIndexOrThrow("fechaFin"))
                val fechaFinDate: Date? = if (fechaFinString != null) {
                    dateFormat.parse(fechaFinString)
                } else {
                    null
                }
                val info = cursor.getString(cursor.getColumnIndexOrThrow("info"))
                val estado =
                    EstadoPrestamo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))

                val prestamo =
                    Prestamo(
                        idPrestamo,
                        idArticulo,
                        idSocio,
                        fechaInicio,
                        fechaFin = fechaFinDate,
                        info,
                        estado
                    )
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

            // Manejar fechaFin: si es null, insertar null en la base de datos
            put(
                "fechaFin",
                if (prestamo.fechaFin != null) dateFormat.format(prestamo.fechaFin) else null
            )

            put("info", prestamo.info)
            put("estado", prestamo.estado.toString())
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
            //  put("fechaFin", dateFormat.format(prestamo.fechaFin))
            put(
                "fechaFin",
                if (prestamo.fechaFin != null) dateFormat.format(prestamo.fechaFin) else null
            )
            put("info", prestamo.info)
            put("estado", prestamo.estado.toString())
        }

        val affectedRows = db.update(
            "prestamos",
            values,
            "idPrestamo = ?",
            arrayOf(prestamo.idPrestamo.toString())
        )
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

    fun obtenerPrestamosPorArticulo(idArticulo: Int): List<Prestamo> {
        val db = readableDatabase
        val listaPrestamos = mutableListOf<Prestamo>()
        try {
            val selectQuery = "SELECT * FROM prestamos WHERE idArticulo = ?"
            db.rawQuery(selectQuery, arrayOf(idArticulo.toString())).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idPrestamo = cursor.getInt(cursor.getColumnIndexOrThrow("idPrestamo"))
                        val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                        val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
                        val fechaInicio =
                            dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")))
                        val fechaFinString =
                            cursor.getString(cursor.getColumnIndexOrThrow("fechaFin"))
                        val fechaFinDate: Date? = if (fechaFinString != null) {
                            dateFormat.parse(fechaFinString)
                        } else {
                            null
                        }
                        val info = cursor.getString(cursor.getColumnIndexOrThrow("info"))
                        val estado =
                            EstadoPrestamo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))

                        val prestamo = Prestamo(
                            idPrestamo,
                            idArticulo,
                            idSocio,
                            fechaInicio,
                            fechaFin = fechaFinDate,
                            info,
                            estado
                        )
                        listaPrestamos.add(prestamo)
                    } while (cursor.moveToNext())
                }
            }
            Log.d(
                TAG,
                "Se obtuvieron ${listaPrestamos.size} préstamos para el artículo con ID: $idArticulo"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener préstamos por artículo: ${e.message}")
            throw RuntimeException("Error obteniendo préstamos por artículo: ${e.message}", e)
        } finally {
            db.close()
        }
        return listaPrestamos
    }

    fun obtenerPrestamosPorSocio(idSocio: Int): List<Prestamo> {
        val db = readableDatabase
        val listaPrestamos = mutableListOf<Prestamo>()
        try {
            val selectQuery = "SELECT * FROM prestamos WHERE idSocio = ?"
            db.rawQuery(selectQuery, arrayOf(idSocio.toString())).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idPrestamo = cursor.getInt(cursor.getColumnIndexOrThrow("idPrestamo"))
                        val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                        val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("idSocio"))
                        val fechaInicio =
                            dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")))
                        val fechaFinString =
                            cursor.getString(cursor.getColumnIndexOrThrow("fechaFin"))
                        val fechaFinDate: Date? = if (fechaFinString != null) {
                            dateFormat.parse(fechaFinString)
                        } else {
                            null
                        }
                        val info = cursor.getString(cursor.getColumnIndexOrThrow("info"))
                        val estado =
                            EstadoPrestamo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))

                        val prestamo = Prestamo(
                            idPrestamo,
                            idArticulo,
                            idSocio,
                            fechaInicio,
                            fechaFin = fechaFinDate,
                            info,
                            estado
                        )
                        listaPrestamos.add(prestamo)
                    } while (cursor.moveToNext())
                }
            }
            Log.d(TAG, "Se obtuvieron ${listaPrestamos.size} préstamos para el socio con ID: $idSocio")
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener préstamos por socio: ${e.message}")
            throw RuntimeException("Error obteniendo préstamos por socio: ${e.message}", e)
        } finally {
            db.close()
        }
        return listaPrestamos
    }

    fun actualizarEstadoPrestamo(idPrestamo: Int, nuevoEstado: EstadoPrestamo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("estado", nuevoEstado.toString())
        }
        val affectedRows =
            db.update("prestamos", values, "idPrestamo = ?", arrayOf(idPrestamo.toString()))
        db.close()
        if (affectedRows > 0) {
            Log.d(TAG, "Estado del préstamo actualizado con ID: $idPrestamo a $nuevoEstado")
        } else {
            Log.d(TAG, "No se pudo actualizar el estado del préstamo con ID: $idPrestamo")
        }
    }

    fun estaArticuloEnPrestamoActivo(idArticulo: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM prestamos WHERE idArticulo = ? AND estado = ?",
            arrayOf(idArticulo.toString(), EstadoPrestamo.ACTIVO.toString())
        )
        val estaEnPrestamo = cursor.count > 0
        cursor.close()
        db.close()
        return estaEnPrestamo
    }

    fun estaSocioEnPrestamoActivo(idSocio: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM prestamos WHERE idSocio = ? AND estado = ?",
            arrayOf(idSocio.toString(), EstadoPrestamo.ACTIVO.toString())
        )
        val estaEnPrestamo = cursor.count > 0
        cursor.close()
        db.close()
        return estaEnPrestamo
    }

    ///PARA MOSTRAR DATOS EN PRESTAMOS
    fun obtenerArticuloPrestamoId(idArticulo: Int): Articulo? {
        return articulosDbHelper.obtenerArticuloPorId(idArticulo)
    }

    fun obtenerSocioPrestamoId(idSocio: Int): Socio? {
        return sociosDbHelper.obtenerSocioPorId(idSocio)
    }
}