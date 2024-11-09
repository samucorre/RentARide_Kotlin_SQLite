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

    fun obtenerArticulos(): List<Articulo> {
        return readableDatabase.use { db -> articulosDbHelper.obtenerArticulos(db) }
    }

    fun obtenerArticuloPorId(idArticulo: Int): Articulo? {
        return readableDatabase.use { db -> articulosDbHelper.obtenerArticuloPorId(db, idArticulo) }
    }

    fun obtenerIdArticuloBD(articulo: Articulo): Int {
        return readableDatabase.use { db -> articulosDbHelper.obtenerIdArticuloBD(db, articulo) }
    }

    fun insertarArticulo(articulo: Articulo): Long {
        return writableDatabase.use { db -> articulosDbHelper.insertarArticulo(db, articulo) }
    }

    fun actualizarArticulo(articulo: Articulo) {
        writableDatabase.use { db -> articulosDbHelper.actualizarArticulo(db, articulo) }
    }

    fun borrarArticulo(idArticulo: Int): Int {
        return writableDatabase.use { db -> articulosDbHelper.borrarArticulo(db, idArticulo) }
    }

    fun obtenerArticulosDisponibles(): List<Articulo> {
        return readableDatabase.use { db -> articulosDbHelper.obtenerArticulosDisponibles(db) }
    }

    fun actualizarEstadoArticulo(idArticulo: Int, nuevoEstado: EstadoArticulo) {
        writableDatabase.use { db -> articulosDbHelper.actualizarEstadoArticulo(db, idArticulo, nuevoEstado) }
    }


    fun reiniciarBaseDeDatos() {
        close() // Cierra la conexión actual a la base de datos
        // Vuelve a abrir la conexión a la base de datos
        // Esto forzará a la aplicación a utilizar la nueva base de datos importada
        writableDatabase
    }

//    fun articuloEnPrestamo(idArticulo: Int): Boolean {
//        return readableDatabase.use { db -> articuloDbHelper.articuloEnPrestamo(db, idArticulo) }
//    }
//
//    fun obtenerCategorias(): List<String> {
//        return readableDatabase.use { db -> articuloDbHelper.obtenerCategorias(db) }
//    }
//
//    fun borrarTodosLosArticulos() {
//        writableDatabase.use { db -> articuloDbHelper.borrarTodosLosArticulos(db) }
//    }
//
//    fun obtenerEstadoArticulo(idArticulo: Int): EstadoArticulo? {
//        return readableDatabase.use { db -> articuloDbHelper.obtenerEstadoArticulo(db, idArticulo) }
//    }
}
