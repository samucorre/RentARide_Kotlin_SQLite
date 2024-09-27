package pf.dam.articulos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import pf.dam.R

class ArticulosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var articulosAdapter: ArticulosAdapter
    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var addArticuloButton: FloatingActionButton
    private lateinit var deleteArticuloButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulos)

        dbHelper = ArticulosSQLite(this)
        recyclerView = findViewById(R.id.articulosRecyclerView)
        addArticuloButton = findViewById(R.id.addArticuloButton)
        backButton = findViewById(R.id.backButton)

        articulosAdapter = ArticulosAdapter(dbHelper.obtenerArticulos())
        recyclerView.adapter = articulosAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        addArticuloButton.setOnClickListener {
            val intent = Intent(this, ArticuloAddActivity::class.java)
            startActivity(intent)
          
        }

       /* deleteArticuloButton.setOnClickListener {
           //COMENTADO,PUEDE BORRAR TODOS LOS ARTICULOS
            // val dbHelper = ArticulosSQLite(this) // Obtener instancia de ArticulosSQLite
            //dbHelper.borrarTodosLosArticulos() // Llamar al método para borrar todos los artículos
            // Actualizar la vista si es necesario (por ejemplo, si usas un RecyclerView)
            //articulosAdapter.articulos = dbHelper.obtenerArticulos()
            //articulosAdapter.notifyDataSetChanged()
            //Toast.makeText(this, "Todos los artículos han sido eliminados", Toast.LENGTH_SHORT).show()

        }*/

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarListaArticulos()
    }

    private fun actualizarListaArticulos() {
        articulosAdapter.articulos = dbHelper.obtenerArticulos()
        articulosAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val articuloId = data?.getIntExtra("idArticulo", -1) ?: -1
            if (articuloId != -1) {
                val articuloActualizado = dbHelper.obtenerArticuloPorId(articuloId)
                if (articuloActualizado != null) {
                    // Actualizar la vista con articuloActualizado
                    articulosAdapter.articulos = dbHelper.obtenerArticulos()
                    articulosAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
