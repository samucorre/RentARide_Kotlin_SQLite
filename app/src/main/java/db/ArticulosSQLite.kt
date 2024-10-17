package db

import pf.dam.articulos.Articulo
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.EstadoPrestamo

class ArticulosSQLite (context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "articulos.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaArticulos = """
            CREATE TABLE articulos(
                idArticulo INTEGER PRIMARY KEY AUTOINCREMENT,
                categoria TEXT ,
                tipo TEXT ,
                nombre TEXT ,
                descripcion TEXT ,
                estado TEXT ,
                rutaImagen TEXT
            )
        """.trimIndent()
        db?.execSQL(crearTablaArticulos)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS articulos")
        onCreate(db)
    }

    fun obtenerArticulos(): List<Articulo> {
        val db = readableDatabase
        val listaArticulos = mutableListOf<Articulo>()

        try {
            val selectedQuery = "SELECT * FROM articulos"
            db.rawQuery(selectedQuery, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                        val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                        val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                        val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                        val descripcion =
                            cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                        val estado =
                            if (cursor.getString(cursor.getColumnIndexOrThrow("estado")) != null) {
                                EstadoArticulo.valueOf(
                                    cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                            "estado"
                                        )
                                    )
                                )
                            } else {
                                null
                            }
                        val rutaImagen =
                            cursor.getString(cursor.getColumnIndexOrThrow("rutaImagen"))

                        val articulo = Articulo(
                            idArticulo,
                            categoria,
                            tipo,
                            nombre,
                            descripcion,
                            estado,
                            rutaImagen
                        )
                        listaArticulos.add(articulo)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Error obteniendo artículos: ${e.message}", e)
        } finally {
            db.close()
        }
        return listaArticulos
    }

    fun obtenerArticuloPorId(idArticulo: Int): Articulo? {
        val db = readableDatabase
        val cursor = db.query(
            "articulos",
            null,
            "idArticulo = ?",
            arrayOf(idArticulo.toString()),
            null,
            null,
            null
        )
        try {
            return if (cursor.moveToFirst()) {
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                val estado = if (cursor.getString(cursor.getColumnIndexOrThrow("estado")) != null) {
                    EstadoArticulo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))
                } else {
                    null
                }
                val rutaImagen = cursor.getString(cursor.getColumnIndexOrThrow("rutaImagen"))

                Articulo(idArticulo, categoria, tipo, nombre, descripcion, estado, rutaImagen)
            } else {
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun obtenerIdArticuloBD(articulo: Articulo): Int {
        val db = readableDatabase
        var articuloId = -1

        try {
            val selectQuery = """
            SELECT idArticulo FROM articulos WHERE 
            (nombre = ? OR nombre IS NULL) AND 
            (categoria = ? OR categoria IS NULL) AND 
            (tipo = ? OR tipo IS NULL) AND 
            (descripcion = ? OR descripcion IS NULL) AND 
            (estado = ? OR estado IS NULL) 
        """
            val parametros = arrayOf(
                articulo.nombre,
                articulo.categoria,
                articulo.tipo,
                articulo.descripcion,
                articulo.estado?.name
            )

            db.rawQuery(selectQuery, parametros).use { cursor ->
                if (cursor.moveToFirst()) {
                    articuloId = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                }
            }
        } catch (e: Exception) {
            Log.e("ArticulosSQLite", "Error al obtener el ID del articulo: ${e.message}")
        } finally {
            db.close()
        }
        return articuloId
        Log.d("CRUDSQLite", "Articulo obtenido: ${articulo} con ID: $articuloId")
    }

    fun insertarArticulo(articulo: Articulo): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("categoria", articulo.categoria)
        values.put("tipo", articulo.tipo)
        values.put("nombre", articulo.nombre)
        values.put("descripcion", articulo.descripcion)
        values.put("estado", articulo.estado?.name)
        values.put("rutaImagen", articulo.rutaImagen)

        val idArticulo = db.insert("articulos", null, values)

        if (idArticulo != -1L) {
            articulo.idArticulo = idArticulo.toInt()
        }

        db.close()
        return idArticulo
    }

    fun actualizarArticulo(articulo: Articulo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("categoria", articulo.categoria)
            put("tipo", articulo.tipo)
            put("nombre", articulo.nombre)
            put("descripcion", articulo.descripcion)
            put("estado", articulo.estado?.name)
            put("rutaImagen", articulo.rutaImagen)
        }

        db.update("articulos", values, "idArticulo = ?", arrayOf(articulo.idArticulo.toString()))
        db.close()
    }

    fun borrarArticulo(idArticulo: Int): Int {
        val db = writableDatabase
        val rowsAffected = db.delete("articulos", "idArticulo = ?", arrayOf(idArticulo.toString()))
        db.close()
        return rowsAffected
    }


    fun borrarTodosLosArticulos() {
        val db = writableDatabase
        db.delete("articulos", null, null)
        db.close()
    }

    fun obtenerEstadoArticulo(idArticulo: Int): EstadoArticulo? {
        val db = readableDatabase
        val cursor = db.query(
            "articulos",
            arrayOf("estado"),
            "idArticulo = ?",
            arrayOf(idArticulo.toString()),
            null,
            null,
            null
        )
        try {
            return if (cursor.moveToFirst()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow("estado")) != null) {
                    EstadoArticulo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))
                } else {
                    null
                }
            } else {
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun obtenerArticulosDisponibles(): List<Articulo> {
        val articulos = mutableListOf<Articulo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM articulos WHERE estado = 'DISPONIBLE'", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                    val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                    val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                    val estado =
                        EstadoArticulo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))
                    val rutaImagen = cursor.getString(cursor.getColumnIndexOrThrow("rutaImagen"))

                    val articulo = Articulo(
                        idArticulo,
                        categoria,
                        tipo,
                        nombre,
                        descripcion,
                        estado,
                        rutaImagen
                    )
                    articulos.add(articulo)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            throw RuntimeException("Error obteniendo artículos disponibles: ${e.message}", e)
        } finally {
            cursor.close()
            db.close()
        }
        return articulos
    }

    fun actualizarEstadoArticulo(idArticulo: Int, nuevoEstado: EstadoArticulo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("estado", nuevoEstado.toString())
        }
        val affectedRows = db.update("articulos", values, "idArticulo = ?", arrayOf(idArticulo.toString()))
        db.close()
        if (affectedRows > 0) {
            Log.d(TAG, "Estado del artículo actualizado con ID: $idArticulo")
        } else {
            Log.d(TAG, "No se pudo actualizar el estado del artículo con ID: $idArticulo")
        }
    }
    fun articuloEnPrestamo(idArticulo: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM prestamos WHERE idArticulo = ? AND estado = ?",
            arrayOf(idArticulo.toString(), EstadoPrestamo.ACTIVO.name)
        )
        val estaEnPrestamo = cursor.count > 0
        cursor.close()
        db.close()
        return estaEnPrestamo
    }
}