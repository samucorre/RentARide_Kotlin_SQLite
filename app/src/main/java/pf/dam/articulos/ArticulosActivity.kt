package pf.dam.articulos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
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
    private lateinit var db: ArticulosSQLite
    private lateinit var addArticuloButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var searchView: SearchView
    private lateinit var estadoRadioGroup: RadioGroup

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.articulos_activity)
        supportActionBar?.title = "RR - Artículos"

        db = ArticulosSQLite(this)
        recyclerView = findViewById(R.id.articulosRecyclerView)
        searchView = findViewById(R.id.searchView)
        addArticuloButton = findViewById(R.id.addArticuloButton)
        homeButton = findViewById(R.id.homeButton)
        estadoRadioGroup = findViewById(R.id.estadoRadioGroup)
        articulosAdapter = ArticulosAdapter(db.getAllArticulos())
        recyclerView.adapter = articulosAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        addArticuloButton.setOnClickListener {
            val intent = Intent(this, ArticuloAddActivity::class.java)
            startActivity(intent)
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
                articulosAdapter.articulos =
                    db.articulosDbHelper.filtrarArticulos(db.readableDatabase, newText)
                articulosAdapter.notifyDataSetChanged()
                return true
            }
        })

        estadoRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val estadoSeleccionado = when (checkedId) {
                R.id.todosRadioButton -> null
                R.id.disponibleRadioButton -> EstadoArticulo.DISPONIBLE
                R.id.prestadoRadioButton -> EstadoArticulo.PRESTADO
                R.id.noDisponibleRadioButton -> EstadoArticulo.NO_DISPONIBLE
                else -> null
            }
            articulosAdapter.articulos = db.articulosDbHelper.filtrarArticulosPorEstado(
                db.readableDatabase,
                estadoSeleccionado
            ) // Llamada a la función
            articulosAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarListaArticulos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val articuloId = data?.getIntExtra("idArticulo", -1) ?: -1
            if (articuloId != -1) {
                val articuloActualizado = db.getArticuloById(articuloId)
                if (articuloActualizado != null) {
                    articulosAdapter.articulos = db.getAllArticulos()
                    articulosAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun actualizarListaArticulos() {
        val db = ArticulosSQLite(this)
        val articulos = db.getAllArticulos()
        articulosAdapter.articulos = articulos
        articulosAdapter.notifyDataSetChanged()
    }
}