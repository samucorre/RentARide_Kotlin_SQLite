package db

import pf.dam.articulos.Articulo
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pf.dam.articulos.EstadoArticulo

class ArticulosSQLite (context: Context):
SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "articulos.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Creamos la tabla articulos
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

        // Creamos la tabla de articulos
        db?.execSQL(crearTablaArticulos)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS articulos")
        onCreate(db)
    }
/*COMENTADO PARA PROBAR IMAGEN
    fun obtenerArticulos(): List<Articulo> {
        val db = readableDatabase // Accedemos a la BBDD en modo lectura
        val listaArticulos = mutableListOf<Articulo>()

        try {
            val selectedQuery = "SELECT * FROM articulos"
            db.rawQuery(selectedQuery, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                      //  val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                        val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                        val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                        val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                        val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                        val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))

                        val articulo = Articulo(
                         //   idArticulo,
                            categoria,
                            tipo,
                            nombre,
                            descripcion,
                            estado
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
    }*/
    fun obtenerArticulos(): List<Articulo> {
    val db = readableDatabase
    val listaArticulos = mutableListOf<Articulo>()

    try {
        val selectedQuery = "SELECT * FROM articulos"
        db.rawQuery(selectedQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
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

                    val articulo = Articulo(
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
    fun obtenerArticuloPorId(idArticulo: Int): Articulo? { //Obtiene un Articulo por su idArticulo
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
try{
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

            Articulo(categoria, tipo, nombre, descripcion, estado, rutaImagen)
        } else {
            null
        }
    }finally
    {
        cursor.close()
        db.close()
    }
    }
   /* fun obtenerIdArticuloBD(articulo: Articulo): Int {
        val db = readableDatabase
        var articuloId = -1

        try {
            val selectQuery = """
            SELECT idArticulo FROM articulos WHERE 
            (nombre = ? OR nombre IS NULL) AND 
            (categoria = ? OR categoria IS NULL) AND 
            (tipo = ? OR tipo IS NULL) AND 
            (descripcion = ? OR descripcion IS NULL) AND 
            (estado = ? OR estado IS NULL) AND 
            (rutaImagen = ? OR rutaImagen IS NULL)
        """
            val parametros = arrayOf(
                articulo.nombre,
                articulo.categoria,
                articulo.tipo,
                articulo.descripcion,
                articulo.estado,
                articulo.rutaImagen
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
    }*/
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
   }
    fun insertarArticulo(articulo: Articulo): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("categoria", articulo.categoria ?: null)
        values.put("tipo", articulo.tipo ?: null)
        values.put("nombre", articulo.nombre ?: null)
        values.put("descripcion", articulo.descripcion ?: null)
        values.put("estado", articulo.estado?.name ?: null)
        values.put("rutaImagen", articulo.rutaImagen ?: "")

        val idArticulo = db.insert("articulos", null, values)
        db.close()
        Log.d(
            "CRUD INSERT", "idArticulo: ${idArticulo} correctamente añadido " +
                    "\n${values}"
        )
        return idArticulo

    }
    fun actualizarArticulo(idArticulo: Int, articulo: Articulo) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("categoria", articulo.categoria ?: null)
            put("tipo", articulo.tipo ?: null)
            put("nombre", articulo.nombre ?: null)
            put("descripcion", articulo.descripcion ?: null)
            put("estado", articulo.estado?.name ?: null)
            put("rutaImagen", articulo.rutaImagen ?: null)
        }

            db.update("articulos", values, "idArticulo = ?", arrayOf(idArticulo.toString()))
            Log.d(
                "CRUD UPDATE", "Artculo Id: ${idArticulo} correctamente actualizado " +
                        "\n${values}"
            )
        db.close()
    }
    fun borrarArticulo(idArticulo: Int): Int {
        val db = writableDatabase
        val rowsAffected = db.delete("articulos", "idArticulo = ?", arrayOf(idArticulo.toString()))
        Log.d("CRUD DELETE", "Articulo id: ${idArticulo} correctamente eliminado")
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
            arrayOf("estado"), // Solo necesitamos la columna "estado"
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

    fun obtenerArticulosDisponibles(): List<Articulo> {    val articulos = mutableListOf<Articulo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM articulos WHERE estado = 'DISPONIBLE'", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                    val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                    val estado = EstadoArticulo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))
                    val rutaImagen = cursor.getString(cursor.getColumnIndexOrThrow("rutaImagen"))

                    val articulo = Articulo(
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

    fun obtenerIdsArticulosDisponibles(): List<Int> {
        val idsArticulos = mutableListOf<Int>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT idArticulo FROM articulos WHERE estado = 'DISPONIBLE'", null)
        while (cursor.moveToNext()) {
            val idArticulo = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
            idsArticulos.add(idArticulo)
        }
        cursor.close()
        return idsArticulos
    }

    fun obtenerIdArticuloDisponibleBD(articulo: Articulo): Int {
        val db = readableDatabase
        var articuloId = -1

        try {
            val selectQuery = """
            SELECT idArticulo FROM articulos WHERE 
            (nombre = ? OR nombre IS NULL) AND 
            (categoria = ? OR categoria IS NULL) AND 
            (tipo = ? OR tipo IS NULL) AND 
            (descripcion = ? OR descripcion IS NULL) AND 
            (estado = 'DISPONIBLE')
        """
            val parametros = arrayOf(
                articulo.nombre,
                articulo.categoria,
                articulo.tipo,
                articulo.descripcion
            )

            db.rawQuery(selectQuery, parametros).use { cursor ->
                if (cursor.moveToFirst()) {
                    articuloId = cursor.getInt(cursor.getColumnIndexOrThrow("idArticulo"))
                    Log.d("ArticulosSQLite", "ID del artículo obtenido: $articuloId")
                }
            }
        } catch (e: Exception) {
            Log.e("ArticulosSQLite", "Error al obtener el ID del articulo: ${e.message}")
        } finally {
            db.close()
        }
        return articuloId
    }

    fun actualizarEstadoArticulo(idArticulo: Int, nuevoEstado: EstadoArticulo): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("estado", nuevoEstado.name)
        }
        val whereClause = "idArticulo = ?"
        val whereArgs = arrayOf(idArticulo.toString())
        return db.update("articulos", values, whereClause, whereArgs)
    }
}