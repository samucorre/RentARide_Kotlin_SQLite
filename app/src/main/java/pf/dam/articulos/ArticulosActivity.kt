package pf.dam.articulos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import pf.dam.MainActivity
import pf.dam.R

class ArticulosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var articulosAdapter: ArticulosAdapter
    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var addArticuloButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var searchView: SearchView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulos)

        dbHelper = ArticulosSQLite(this)
        recyclerView = findViewById(R.id.articulosRecyclerView)
        searchView = findViewById(R.id.searchView)
        addArticuloButton = findViewById(R.id.addArticuloButton)
        backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.homeButton)

        articulosAdapter = ArticulosAdapter(dbHelper.obtenerArticulos())
        recyclerView.adapter = articulosAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        addArticuloButton.setOnClickListener {
            val intent = Intent(this, ArticuloAddActivity::class.java)
            startActivity(intent)

        }
        backButton.setOnClickListener {
            finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarArticulos(newText)
                return true
            }
        })
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

    private fun filtrarArticulos(query: String?) {
        val articulosFiltrados = if (query.isNullOrEmpty()) {
            dbHelper.obtenerArticulos()
        } else {
            dbHelper.obtenerArticulos().filter { articulo ->
                articulo.nombre?.contains(query, ignoreCase = true) ?: false ||
                        articulo.categoria?.contains(query, ignoreCase = true) ?: false ||
                        articulo.tipo?.contains(query, ignoreCase = true) ?: false
            }
        }
        articulosAdapter.articulos = articulosFiltrados
        articulosAdapter.notifyDataSetChanged()
    }
}

