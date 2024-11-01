package db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pf.dam.socios.Socio

class SociosSQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "socios.db"
        private const val DATABASE_VERSION = 1
    }

    private val socioDbHelper = SociosDbHelper(this)

    override fun onCreate(db: SQLiteDatabase) {
        val crearTablaSocios = """
            CREATE TABLE socios (
                idSocio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                numeroSocio INTEGER,
                telefono INTEGER,
                email TEXT
            )
        """.trimIndent()
        db.execSQL(crearTablaSocios)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS socios")
        onCreate(db)
    }

    fun obtenerSocios(): List<Socio> {
        return readableDatabase.use { db -> socioDbHelper.obtenerSocios(db) }
    }

    fun obtenerSocioPorId(idSocio: Int): Socio? {
        return readableDatabase.use { db -> socioDbHelper.obtenerSocioPorId(db, idSocio) }
    }

    fun insertarSocio(socio: Socio): Long {
        return writableDatabase.use { db -> socioDbHelper.insertarSocio(db, socio) }
    }

    fun actualizarSocio(socio: Socio) {
        writableDatabase.use { db -> socioDbHelper.actualizarSocio(db, socio) }
    }

    fun borrarSocio(idSocio: Int): Int {
        return writableDatabase.use { db -> socioDbHelper.borrarSocio(db, idSocio) }
    }
}