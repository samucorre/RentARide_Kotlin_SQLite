package db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pf.dam.socios.Socio
import java.util.Date

class SociosSQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "socios.db"
        private const val DATABASE_VERSION = 3
        private const val TAG = "SociosSQLite"
    }

    private val socioDbHelper = SociosDbHelper(this)

    override fun onCreate(db: SQLiteDatabase) {
        val crearTablaSocios = """
            CREATE TABLE socios (
                idSocio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                numeroSocio INTEGER UNIQUE,
                telefono INTEGER,
                email TEXT,
                fechaNacimiento TEXT,  
                fechaIngresoSocio TEXT,
                genero TEXT,
                softDeletedSocio INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(crearTablaSocios)
        db.execSQL("PRAGMA foreign_keys = ON;")
        Log.d(TAG, "Tabla socios creada")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
        db.execSQL("DROP TABLE IF EXISTS socios")
        onCreate(db)
        Log.d(TAG, "Tabla articulos actualizada")
    }
    }

    fun getAllSocios(): List<Socio> {
        return readableDatabase.use { db -> socioDbHelper.getAllSocios(db) }
    }

    fun getSocioById(idSocio: Int): Socio? {
        return readableDatabase.use { db -> socioDbHelper.getSocioById(db, idSocio) }
    }

    fun addSocio(socio: Socio): Long {
        return writableDatabase.use { db -> socioDbHelper.addSocio(db, socio) }
    }

    fun updateSocio(socio: Socio) {
        writableDatabase.use { db -> socioDbHelper.updateSocio(db, socio) }
    }

    fun deleteSocio(idSocio: Int): Int {
        return writableDatabase.use { db -> socioDbHelper.deleteSocio(db, idSocio) }
    }

    fun getSociosCumpleanosMes(): List<Pair<String, Date>> {
        return readableDatabase.use { db -> socioDbHelper.getSociosCumpleanosMes(db) }
    }

    fun getUltimoSocioRegistrado(): Socio? {
        return readableDatabase.use { db -> socioDbHelper.getUltimoSocioRegistrado(db) }
    }


}