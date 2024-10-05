package pf.dam.prestamos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.PrestamosSQLite
import pf.dam.PrestamosAdapter
import pf.dam.R

class PrestamosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var prestamosAdapter: PrestamosAdapter
    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var addPrestamoButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamos) // Asegúrate de tener este layout

        dbHelper = PrestamosSQLite(this)
        recyclerView = findViewById(R.id.prestamosRecyclerView)
        addPrestamoButton = findViewById(R.id.addPrestamoButton)
        backButton = findViewById(R.id.backButton)// Asegúrate de tener este ID en tu layout

        prestamosAdapter = PrestamosAdapter(dbHelper.obtenerPrestamos())
        recyclerView.adapter = prestamosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addPrestamoButton.setOnClickListener {
            val intent = Intent(this, PrestamoAddActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        actualizarListaPrestamos()
    }

    private fun actualizarListaPrestamos() {
        prestamosAdapter.prestamos = dbHelper.obtenerPrestamos()
        prestamosAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val prestamoId = data?.getIntExtra("idPrestamo", -1) ?: -1
            if (prestamoId != -1) {
                val prestamoActualizado = dbHelper.obtenerPrestamoPorId(prestamoId)
                if (prestamoActualizado != null) {
                    // Actualizar la vista con articuloActualizado
                    prestamosAdapter.prestamos = dbHelper.obtenerPrestamos()
                    prestamosAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
