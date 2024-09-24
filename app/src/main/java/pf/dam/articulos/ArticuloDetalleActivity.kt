package pf.dam.articulos

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import pf.dam.R

class ArticuloDetalleActivity : AppCompatActivity() {
    private lateinit var editArticuloButton: FloatingActionButton
    private lateinit var deleteArticuloButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var dbHelper: ArticulosSQLite

    private lateinit var nombreTextView: TextView
    private lateinit var categoriaTextView: TextView
    private lateinit var tipoTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadoTextView: TextView

    private var articuloId: Int = -1

    private val editArticuloLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val dbHelper = ArticulosSQLite(this) // Inicializar dbHelper aquí
            val articuloActualizado = dbHelper.obtenerArticuloPorId(articuloId)
            if (articuloActualizado != null) {
                mostrarArticulo(articuloActualizado)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulo_detail)

        dbHelper = ArticulosSQLite(this)

        editArticuloButton = findViewById(R.id.editArticuloButton)
        deleteArticuloButton = findViewById(R.id.deleteArticuloButton)
        backButton = findViewById(R.id.backButton)

        nombreTextView = findViewById(R.id.nombreTextView)
        categoriaTextView = findViewById(R.id.categoriaTextView)
        tipoTextView = findViewById(R.id.tipoTextView)
        descripcionTextView = findViewById(R.id.descripcionTextView)
        estadoTextView = findViewById(R.id.estadoTextView)

        articuloId = intent.getIntExtra("idArticulo", -1)
        val articulo = dbHelper.obtenerArticuloPorId(articuloId)

        if (articulo != null) {
            mostrarArticulo(articulo)

            editArticuloButton.setOnClickListener {
                val intent = Intent(this, ArticuloEditActivity::class.java)
                intent.putExtra("articuloId", articuloId)
                editArticuloLauncher.launch(intent)
            }

            val editArticuloLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    setResult(RESULT_OK, Intent().putExtra("articuloId", articuloId))
                    finish()
                }
            }
            deleteArticuloButton.setOnClickListener {
                dbHelper.borrarArticulo(articuloId)
                Toast.makeText(this, "Artículo eliminado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }

            backButton.setOnClickListener {
                finish()
            }
        } else {
            Toast.makeText(this, "Artículo no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun mostrarArticulo(articulo: Articulo) {
        nombreTextView.text = articulo.nombre
        categoriaTextView.text = articulo.categoria
        tipoTextView.text = articulo.tipo
        descripcionTextView.text = articulo.descripcion
        estadoTextView.text = articulo.estado
    }
}