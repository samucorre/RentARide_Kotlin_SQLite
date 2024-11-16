package db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pf.dam.articulos.Articulo
import pf.dam.articulos.EstadoArticulo

class ArticulosSQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "articulos.db"
        private const val DATABASE_VERSION = 1
    }

     val articulosDbHelper = ArticulosDbHelper(this)

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

    fun getAllArticulos(): List<Articulo> {
        return readableDatabase.use { db -> articulosDbHelper.getAllArticulos(db) }
    }

    fun getArticuloById(idArticulo: Int): Articulo? {
        return readableDatabase.use { db -> articulosDbHelper.getArticuloById(db, idArticulo) }
    }

    fun getIdArticuloBd(articulo: Articulo): Int {
        return readableDatabase.use { db -> articulosDbHelper.getIdArticuloBD(db, articulo) }
    }

    fun addArticulo(articulo: Articulo): Long {
        return writableDatabase.use { db -> articulosDbHelper.addArticulo(db, articulo) }
    }

    fun updateArticulo(articulo: Articulo) {
        writableDatabase.use { db -> articulosDbHelper.updateArticulo(db, articulo) }
    }

    fun deleteArticulo(idArticulo: Int): Int {
        return writableDatabase.use { db -> articulosDbHelper.deleteArticulo(db, idArticulo) }
    }

    fun obtenerArticulosDisponibles(): List<Articulo> {
        return readableDatabase.use { db -> articulosDbHelper.getArticulosDisponibles(db) }
    }

    fun actualizarEstadoArticulo(idArticulo: Int, nuevoEstado: EstadoArticulo) {
        writableDatabase.use { db -> articulosDbHelper.actualizarEstadoArticulo(db, idArticulo, nuevoEstado) }
    }

}
