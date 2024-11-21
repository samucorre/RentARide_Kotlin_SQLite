package db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import pf.dam.articulos.Articulo
import pf.dam.articulos.EstadoArticulo

class ArticulosDbHelper(private val dbHelper: ArticulosSQLite) {

    @SuppressLint("Range")
    private fun Cursor.toArticulo(): Articulo {
        return Articulo(
            getInt(getColumnIndexOrThrow("idArticulo")),
            getString(getColumnIndex("categoria")) ?: "",
            getString(getColumnIndex("tipo")) ?: "",
            getString(getColumnIndex("nombre")) ?: "",
            getString(getColumnIndex("descripcion")) ?: "",
            getString(getColumnIndex("estado"))?.let { EstadoArticulo.valueOf(it) },
            getString(getColumnIndex("rutaImagen")) ?: "",
            getInt(getColumnIndexOrThrow("softDeletedArticulo")) == 1 // Mapear softDeletedArticulo
        )
    }

    fun getAllArticulos(db: SQLiteDatabase): List<Articulo> {
        val listaArticulos = mutableListOf<Articulo>()
        db.rawQuery("SELECT * FROM articulos WHERE softDeletedArticulo = 0", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    listaArticulos.add(cursor.toArticulo())
                } while (cursor.moveToNext())
            }
        }
        return listaArticulos
    }

    fun getArticuloById(db: SQLiteDatabase, idArticulo: Int): Articulo? {
        return db.query(
            "articulos", null, "idArticulo = ?", arrayOf(idArticulo.toString()),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.toArticulo() else null
        }
    }

    fun getIdArticuloBD(db: SQLiteDatabase, articulo: Articulo): Int {
        var articuloId = -1
        try {
            val selectQuery = """
            SELECT idArticulo FROM articulos WHERE 
            (nombre = ? OR nombre IS NULL) AND 
            (categoria = ? OR categoria IS NULL) AND 
            (tipo = ? OR tipo IS NULL) AND 
            (descripcion = ? OR descripcion IS NULL) AND 
            (estado = ? OR estado IS NULL) AND
            softDeletedArticulo = 0 
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
        }
        return articuloId
    }

    fun addArticulo(db: SQLiteDatabase, articulo: Articulo): Long {
        val values = ContentValues()
        values.put("categoria", articulo.categoria)
        values.put("tipo", articulo.tipo)
        values.put("nombre", articulo.nombre)
        values.put("descripcion", articulo.descripcion)
        values.put("estado", articulo.estado?.name)
        values.put("rutaImagen", articulo.rutaImagen)
        values.put("softDeletedArticulo", 0)

        val idArticulo = db.insert("articulos", null, values)
        if (idArticulo != -1L) {
            articulo.idArticulo = idArticulo.toInt()
        }
        return idArticulo
    }

    fun updateArticulo(db: SQLiteDatabase, articulo: Articulo) {
        val values = ContentValues().apply {
            put("categoria", articulo.categoria)
            put("tipo", articulo.tipo)
            put("nombre", articulo.nombre)
            put("descripcion", articulo.descripcion)
            put("estado", articulo.estado?.name)
            put("rutaImagen", articulo.rutaImagen)
        }
        db.update("articulos", values, "idArticulo = ? AND softDeletedArticulo = 0", arrayOf(articulo.idArticulo.toString()))
    }

    fun deleteArticulo(db: SQLiteDatabase, idArticulo: Int): Int {
        val values = ContentValues().apply { put("softDeletedArticulo", 1) }
        return db.update("articulos", values, "idArticulo = ?", arrayOf(idArticulo.toString()))
    }

    fun getArticulosDisponibles(db: SQLiteDatabase): List<Articulo> {
        val articulos = mutableListOf<Articulo>()
        db.rawQuery("SELECT * FROM articulos WHERE estado = 'DISPONIBLE' AND softDeletedArticulo = 0", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    articulos.add(cursor.toArticulo())
                } while (cursor.moveToNext())
            }
        }
        return articulos
    }

    fun actualizarEstadoArticulo(db: SQLiteDatabase, idArticulo: Int, nuevoEstado: EstadoArticulo) {
        val values = ContentValues().apply { put("estado", nuevoEstado.toString()) }
        val affectedRows =
            db.update("articulos", values, "idArticulo = ? AND softDeletedArticulo = 0", arrayOf(idArticulo.toString()))
        if (affectedRows > 0) {
            Log.d(ContentValues.TAG, "Estado del artículo actualizado con ID: $idArticulo")
        } else {
            Log.d(
                ContentValues.TAG,
                "No se pudo actualizar el estado del artículo con ID: $idArticulo"
            )
        }
    }

    fun filtrarArticulos(db: SQLiteDatabase, query: String?): List<Articulo> {
        return if (query.isNullOrEmpty()) {
            getAllArticulos(db)
        } else {
            getAllArticulos(db).filter { articulo ->
                articulo.nombre?.contains(query, ignoreCase = true) ?: false ||
                        articulo.categoria?.contains(query, ignoreCase = true) ?: false ||
                        articulo.tipo?.contains(query, ignoreCase = true) ?: false
            }
        }
    }

    fun filtrarArticulosPorEstado(db: SQLiteDatabase, estado: EstadoArticulo?): List<Articulo> {
        return if (estado == null) {
            getAllArticulos(db)
        } else {
            getAllArticulos(db).filter { articulo -> articulo.estado == estado }
        }
    }
}
