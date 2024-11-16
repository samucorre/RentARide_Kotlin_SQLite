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

    fun getAllPrestamos(): List<Prestamo> {
        return readableDatabase.use { db -> prestamoDbHelper.getAllPrestamos(db) }
    }

    fun getPrestamoById(idPrestamo: Int): Prestamo? {
        return readableDatabase.use { db -> prestamoDbHelper.getPrestamoById(db, idPrestamo) }
    }

    fun addrPrestamo(prestamo: Prestamo): Long {
        return writableDatabase.use { db -> prestamoDbHelper.addPrestamo(db, prestamo) }
    }

    fun updatePrestamo(prestamo: Prestamo) {
        writableDatabase.use { db -> prestamoDbHelper.updatePrestamo(db, prestamo) }
    }

    fun deletePrestamo(idPrestamo: Int): Int {
        return writableDatabase.use { db -> prestamoDbHelper.deletePrestamo(db, idPrestamo) }
    }

    fun getPrestamosByArticulo(idArticulo: Int): List<Prestamo> {
        return readableDatabase.use { db -> prestamoDbHelper.getPrestamosByArticulo(db, idArticulo) }
    }

    fun getPrestamosBySocio(idSocio: Int): List<Prestamo> {
        return readableDatabase.use { db -> prestamoDbHelper.getPrestamosBySocio(db, idSocio) }
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

    fun getPrestamoByIdArticulo(idArticulo: Int): Articulo? {
        return articulosDbHelper.getArticuloById(idArticulo)
    }

    fun getPrestamoByIdSocio(idSocio: Int): Socio? {
        return sociosDbHelper.getSocioById(idSocio)
    }

    fun getPrestamoByCategoria(idArticulo: Int): String? {
        val articulo = articulosDbHelper.getArticuloById(idArticulo)
        return articulo?.categoria
    }

    fun obtenerUltimoPrestamoPorArticulo(idArticulo: Int): Prestamo? {
        return readableDatabase.use { db -> prestamoDbHelper.getUltimoPrestamoPorArticulo(db, idArticulo) }
    }

    fun getIdSocioPrestamoActivo(idArticulo: Int): Int? {
        val dbHelper = PrestamosDbHelper(this)
        return dbHelper.getIdSocioPrestamoActivo(writableDatabase, idArticulo)
    }

    fun getSociosConPrestamosActivos(context: Context): List<Socio> {
        val sociosIds = readableDatabase.use { db -> prestamoDbHelper.getSociosPrestamosActivos(db) }
        val sociosSQLite = SociosSQLite(context)
        return sociosSQLite.getAllSocios().filter { socio -> socio.idSocio in sociosIds }
    }

    fun getSociosConPrestamosCerrados(context: Context): List<Socio> {
        val sociosIds = readableDatabase.use { db -> prestamoDbHelper.getSociosPrestamosCerrados(db) }
        val sociosSQLite = SociosSQLite(context)
        return sociosSQLite.getAllSocios().filter { socio -> socio.idSocio in sociosIds }
    }



}