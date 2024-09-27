package pf.dam.socios

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.SociosSQLite
import pf.dam.R

class SociosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sociosAdapter: SociosAdapter
    private lateinit var dbHelper: SociosSQLite
    private lateinit var addSocioButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socios)

        dbHelper = SociosSQLite(this)
        recyclerView = findViewById(R.id.sociosRecyclerView)
        addSocioButton = findViewById(R.id.addSocioButton)
        backButton = findViewById(R.id.backButton)

        sociosAdapter = SociosAdapter(dbHelper.obtenerSocios())
        recyclerView.adapter = sociosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addSocioButton.setOnClickListener{
            val intent = Intent(this, SocioAddActivity::class.java)
            startActivity(intent)
        }

    backButton.setOnClickListener {
        finish()
    }

}
    override fun onResume() {
        super.onResume()
        actualizarListaSocios()
    }

    private fun actualizarListaSocios() {
        sociosAdapter.socios = dbHelper.obtenerSocios()
        sociosAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val socioId = data?.getIntExtra("idSocio", -1) ?: -1
            if (socioId != -1) {
                val socioActualizado = dbHelper.obtenerSocioPorId(socioId)
                if (socioActualizado != null) {
                    // Actualizar la vista con articuloActualizado
                    sociosAdapter.socios = dbHelper.obtenerSocios()
                    sociosAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
