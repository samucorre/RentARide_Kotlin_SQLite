package pf.dam

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class ImportExportActivity : AppCompatActivity() {

    private val CODIGO_SOLICITUD_PERMISO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_import)

        val exportarButton = findViewById<Button>(R.id.exportarButton)
        val importarButton = findViewById<Button>(R.id.importarButton)

        exportarButton.setOnClickListener {
            if (tienePermisos()) {
                exportarBaseDeDatos(this)
            } else {
                solicitarPermisos()
            }
        }

        importarButton.setOnClickListener {
            if (tienePermisos()) {
                importarBaseDeDatos(this)
            } else {
                solicitarPermisos()
            }
        }
    }

    private fun tienePermisos(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun solicitarPermisos() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), CODIGO_SOLICITUD_PERMISO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODIGO_SOLICITUD_PERMISO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportarBaseDeDatos(context: Context) {
        val nombreArchivo = "prestamos.db"
        val directorio = context.getExternalFilesDir(null)
        val archivoOrigen = context.getDatabasePath(nombreArchivo)
        val archivoDestino = File(directorio, nombreArchivo)

        try {
            archivoOrigen.copyTo(archivoDestino, overwrite = true)
            Toast.makeText(context, "Base de datos exportada a: ${archivoDestino.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("Error", "Error al exportar la base de datos: ${e.message}")
            Toast.makeText(context, "Error al exportar la base de datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importarBaseDeDatos(context: Context) {
        val nombreArchivo = "proba.db"
        val directorio = context.getExternalFilesDir(null)
        val archivoOrigen = File(directorio, nombreArchivo)
        val archivoDestino = context.getDatabasePath(nombreArchivo)

        try {
            archivoOrigen.copyTo(archivoDestino, overwrite = true)
            Toast.makeText(context, "Base de datos importada desde: ${archivoOrigen.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("Error", "Error al importar la base de datos: ${e.message}")
            Toast.makeText(context, "Error al importar la base de datos", Toast.LENGTH_SHORT).show()
        }
    }
}