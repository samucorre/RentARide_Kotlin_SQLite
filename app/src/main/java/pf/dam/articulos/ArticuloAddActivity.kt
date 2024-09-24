package pf.dam.articulos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import db.ArticulosSQLite
import pf.dam.R

class ArticuloAddActivity : AppCompatActivity() {
    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var nombreEditText: EditText
    private lateinit var categoriaEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var estadoEditText: EditText
    private lateinit var guardarButton: Button
    private lateinit var volverButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulo_add)

        dbHelper = ArticulosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        categoriaEditText = findViewById(R.id.categoriaEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        descripcionEditText = findViewById(R.id.descripcionEditText)
        estadoEditText = findViewById(R.id.estadoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)

        volverButton.setOnClickListener {finish()}
        guardarButton.setOnClickListener {
            val nuevoArticulo = Articulo(
                categoriaEditText.text.toString(),
                tipoEditText.text.toString(),
                nombreEditText.text.toString(),
                descripcionEditText.text.toString(),
                estadoEditText.text.toString()
            )
            val idNuevoArticulo = dbHelper.insertarArticulo(nuevoArticulo)
            if (idNuevoArticulo != -1L) {
                Toast.makeText(this, "Artículo añadido correctamente", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad después de añadir
            } else {
                Toast.makeText(this, "Error al añadir el artículo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}