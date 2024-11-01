package pf.dam.socios

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.test.filter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import kotlin.text.contains

class SociosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var sociosAdapter: SociosAdapter
    private lateinit var db: SociosSQLite
    private lateinit var addSocioButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socios)

        db = SociosSQLite(this)
        recyclerView = findViewById(R.id.sociosRecyclerView)
        searchView = findViewById(R.id.searchView)
        addSocioButton = findViewById(R.id.addSocioButton)
      //  backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.homeButton)

        sociosAdapter = SociosAdapter(db.obtenerSocios())
        recyclerView.adapter = sociosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportActionBar?.title = "RR - Socios"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val sociosFiltrados = db.obtenerSocios().filter { socio -> // <-- Cambio aquí
                    socio.nombre.contains(newText.orEmpty(), ignoreCase = true) ||
                            socio.apellido.contains(newText.orEmpty(), ignoreCase = true) ||
                            socio.numeroSocio.toString().contains(newText.orEmpty(), ignoreCase = true)||
                            socio.telefono.toString().contains(newText.orEmpty(), ignoreCase = true)||
                            socio.email.contains(newText.orEmpty(), ignoreCase = true)
                }
                sociosAdapter.socios = sociosFiltrados // <-- Cambio aquí
                sociosAdapter.notifyDataSetChanged() // <-- Cambio aquí
                return true
            }
        })
        addSocioButton.setOnClickListener{
            val intent = Intent(this, SocioAddActivity::class.java)
            startActivity(intent)
        }


     //   backButton.setOnClickListener {
     //   finish()
  //  }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

}
    override fun onResume() {
        super.onResume()
        actualizarListaSocios()
    }

    private fun actualizarListaSocios() {
        sociosAdapter.socios = db.obtenerSocios()
        sociosAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val socioId = data?.getIntExtra("idSocio", -1) ?: -1
            if (socioId != -1) {
                val socioActualizado = db.obtenerSocioPorId(socioId)
                if (socioActualizado != null) {
                    // Actualizar la vista con articuloActualizado
                    sociosAdapter.socios = db.obtenerSocios()
                    sociosAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
