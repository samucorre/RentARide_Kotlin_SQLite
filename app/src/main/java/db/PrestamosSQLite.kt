package db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pf.dam.articulos.Articulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.Prestamo
import pf.dam.socios.Socio

class PrestamosSQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "prestamos.db"
        private const val DATABASE_VERSION = 2 // Incrementa la versión de la base de datos
        private const val TAG = "PrestamosSQLite"
    }

    private  var articulosDbHelper: ArticulosSQLite
    private  var sociosDbHelper: SociosSQLite

    init {
        articulosDbHelper = ArticulosSQLite(context.applicationContext)
        sociosDbHelper = SociosSQLite(context.applicationContext)
    }

    private val prestamoDbHelper = PrestamosDbHelper(this)

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
                FOREIGN KEY (idSocio) REFERENCES socios (idSocio) ON DELETE CASCADE,
                FOREIGN KEY (idArticulo) REFERENCES articulos (idArticulo) ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(crearTablaPrestamos)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS prestamos")
            Log.d(TAG, "Tabla prestamos eliminada")
            onCreate(db)
        }
    }

    fun obtenerPrestamos(): List<Prestamo> {
        return readableDatabase.use { db -> prestamoDbHelper.obtenerPrestamos(db) }
    }

    fun obtenerPrestamoPorId(idPrestamo: Int): Prestamo? {
        return readableDatabase.use { db -> prestamoDbHelper.obtenerPrestamoPorId(db, idPrestamo) }
    }

    fun insertarPrestamo(prestamo: Prestamo): Long {
        return writableDatabase.use { db -> prestamoDbHelper.insertarPrestamo(db, prestamo) }
    }

    fun actualizarPrestamo(prestamo: Prestamo) {
        writableDatabase.use { db -> prestamoDbHelper.actualizarPrestamo(db, prestamo) }
    }

    fun borrarPrestamo(idPrestamo: Int): Int {
        return writableDatabase.use { db -> prestamoDbHelper.borrarPrestamo(db, idPrestamo) }
    }

    fun obtenerPrestamosPorArticulo(idArticulo: Int): List<Prestamo> {
        return readableDatabase.use { db -> prestamoDbHelper.obtenerPrestamosPorArticulo(db, idArticulo) }
    }

    fun obtenerPrestamosPorSocio(idSocio: Int): List<Prestamo> {
        return readableDatabase.use { db -> prestamoDbHelper.obtenerPrestamosPorSocio(db, idSocio) }
    }

    fun actualizarEstadoPrestamo(idPrestamo: Int, nuevoEstado: EstadoPrestamo) {
        writableDatabase.use { db -> prestamoDbHelper.actualizarEstadoPrestamo(db, idPrestamo, nuevoEstado) }
    }

    fun estaArticuloEnPrestamoActivo(idArticulo: Int): Boolean {
        return readableDatabase.use { db -> prestamoDbHelper.estaArticuloEnPrestamoActivo(db, idArticulo) }
    }

    fun estaSocioEnPrestamoActivo(idSocio: Int): Boolean {
        return readableDatabase.use { db -> prestamoDbHelper.estaSocioEnPrestamoActivo(db, idSocio) }
    }

    fun obtenerArticuloPrestamoId(idArticulo: Int): Articulo? {
        return articulosDbHelper.obtenerArticuloPorId(idArticulo)
    }

    fun obtenerSocioPrestamoId(idSocio: Int): Socio? {
        return sociosDbHelper.obtenerSocioPorId(idSocio)
    }

    fun obtenerCategoriaPrestamoId(idArticulo: Int): String? {
        val articulo = articulosDbHelper.obtenerArticuloPorId(idArticulo)
        return articulo?.categoria
    }

    fun obtenerUltimoPrestamoPorArticulo(idArticulo: Int): Prestamo? {
        return readableDatabase.use { db -> prestamoDbHelper.obtenerUltimoPrestamoPorArticulo(db, idArticulo) }
    }

    fun obtenerIdSocioPrestamoActivo(idArticulo: Int): Int? {
        val dbHelper = PrestamosDbHelper(this)
        return dbHelper.obtenerIdSocioPrestamoActivo(writableDatabase, idArticulo)
    }
}