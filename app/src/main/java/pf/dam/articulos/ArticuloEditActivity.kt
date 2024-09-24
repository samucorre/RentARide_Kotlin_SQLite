package pf.dam.articulos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import db.ArticulosSQLite
import pf.dam.R

class ArticuloEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var nombreEditText: EditText
    private lateinit var categoriaEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var estadoEditText: EditText
    private lateinit var guardarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulo_edit)

        dbHelper = ArticulosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        categoriaEditText = findViewById(R.id.categoriaEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        descripcionEditText = findViewById(R.id.descripcionEditText)
        estadoEditText = findViewById(R.id.estadoEditText)
        guardarButton = findViewById(R.id.guardarButton)

        val articuloId = intent.getIntExtra("articuloId", -1)
        val articulo = dbHelper.obtenerArticuloPorId(articuloId)

        if (articulo != null) {
            nombreEditText.setText(articulo.nombre)
            categoriaEditText.setText(articulo.categoria)
            tipoEditText.setText(articulo.tipo)
            descripcionEditText.setText(articulo.descripcion)
            estadoEditText.setText(articulo.estado)

            guardarButton.setOnClickListener {
                val articuloActualizado = Articulo(
                  //  articulo.idArticulo,
                    categoriaEditText.text.toString(),
                    tipoEditText.text.toString(),
                    nombreEditText.text.toString(),
                    descripcionEditText.text.toString(),
                    estadoEditText.text.toString()
                )
                dbHelper.actualizarArticulo(articuloId, articuloActualizado)
                Toast.makeText(this, "Artículo actualizado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        } else {
            Toast.makeText(this, "Artículo no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}