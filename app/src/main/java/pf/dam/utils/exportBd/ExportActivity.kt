package pf.dam.utils.exportBd

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pf.dam.MainActivity
import pf.dam.R
import java.io.File

class ExportActivity : AppCompatActivity() {

    private val CODIGO_SOLICITUD_PERMISO = 1

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODIGO_SOLICITUD_PERMISO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.export_activity)

        val exportarButton = findViewById<Button>(R.id.exportarButton)
        val homeButton=findViewById<FloatingActionButton>(R.id.homeButton)
        val volverButton=findViewById<FloatingActionButton>(R.id.volverButton)

        exportarButton.setOnClickListener {
            if (tienePermisos()) {
                exportarBasesDeDatos(this)
            } else {
                solicitarPermisos()
            }
        }

        homeButton.setOnClickListener {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

        volverButton.setOnClickListener {
        finish()
    }

    }

    private fun tienePermisos(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun solicitarPermisos() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            CODIGO_SOLICITUD_PERMISO
        )
    }

    private fun exportarBasesDeDatos(context: Context) {
        val nombreCarpeta = "databases"
        val directorioOrigen = context.getDatabasePath(nombreCarpeta).parentFile
        val directorioDestino = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nombreCarpeta) // Cambia la ruta de destino

        try {
            directorioOrigen?.copyRecursively(directorioDestino, overwrite = true)
            Toast.makeText(
                context,
                "Carpeta de bases de datos exportada a: ${directorioDestino.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e("Error", "Error al exportar la carpeta de bases de datos: ${e.message}")
            Toast.makeText(
                context,
                "Error al exportar la carpeta de bases de datos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}