package db

import pf.dam.articulos.Articulo
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

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
                categoria TEXT NOT NULL,
                tipo TEXT NOT NULL,
                nombre TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                estado TEXT NOT NULL,
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
                    val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
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
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
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

    fun obtenerIdArticuloBD(articulo: Articulo): Int { //Obtener el idArticulo de un artículo a partir de todos sus demás campos. Recycler View
        val db = readableDatabase
        var articuloId = -1 // Valor por defecto si no se encuentra el habitante

        try {
            val selectQuery =
                "SELECT idArticulo FROM articulos WHERE nombre = ? AND categoria = ? AND tipo = ? AND descripcion = ? AND estado  = ? AND rutaImagen = ?"
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
            // Manejar cualquier error aquí
            Log.e("ArticulosSQLite", "Error al obtener el ID del articulo: ${e.message}")
        } finally {
            db.close()
        }
         return articuloId  //+1
    }

    fun insertarArticulo(articulo: Articulo): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("categoria", articulo.categoria)
        values.put("tipo", articulo.tipo)
        values.put("nombre", articulo.nombre)
        values.put("descripcion", articulo.descripcion)
        values.put("estado", articulo.estado)
        values.put("rutaImagen", articulo.rutaImagen)

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
            put("categoria", articulo.categoria)
            put("tipo", articulo.tipo)
            put("nombre", articulo.nombre)
            put("descripcion", articulo.descripcion)
            put("estado", articulo.estado)
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
}