package pf.dam.articulos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.semantics.text
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
    private lateinit var categoriasSpinner: Spinner
    private var primeraSeleccion = true

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
        categoriasSpinner = findViewById(R.id.categoriasSpinner) // Inicializa categoriasSpinner

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

        // Configura el Spinner de categorías
        val categorias = dbHelper.obtenerCategorias() // Obtén las categorías de tu base de datos
        val categoriasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        categoriasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriasSpinner.adapter = categoriasAdapter


        categoriasSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (primeraSeleccion) {
                    primeraSeleccion = false // Desactiva la primera selección
                } else {
                    val categoriaSeleccionada = categorias[position] // Usa la variable categorias existente
                    if (categoriaSeleccionada != "Todos") { // Verifica si la categoría es "Todas las categorías"
                        filtrarArticulosPorCategoria(categoriaSeleccionada)
                    } else {
                        // No filtres la lista de artículos si la categoría es "Todas las categorías"
                        articulosAdapter.articulos = dbHelper.obtenerArticulos() // Muestra todos los artículos
                        articulosAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se ha seleccionado ninguna categoría
                // Puedes agregar aquí alguna acción si lo deseas, como mostrar un mensaje al usuario
            }
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

    private fun filtrarArticulosPorCategoria(categoria: String) {
        val articulosFiltrados = dbHelper.obtenerArticulos().filter { articulo ->
            articulo.categoria == categoria
        }
        articulosAdapter.articulos = articulosFiltrados
        articulosAdapter.notifyDataSetChanged()
    }
}