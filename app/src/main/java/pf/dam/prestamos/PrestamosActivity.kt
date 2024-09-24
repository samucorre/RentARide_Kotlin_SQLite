package pf.dam.prestamos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.PrestamosSQLite
import pf.dam.PrestamosAdapter
import pf.dam.R

class PrestamosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var prestamosAdapter: PrestamosAdapter
    private lateinit var dbHelper: PrestamosSQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamos) // Asegúrate de tener este layout

        dbHelper = PrestamosSQLite(this)
        recyclerView = findViewById(R.id.prestamosRecyclerView) // Asegúrate de tener este ID en tu layout

        prestamosAdapter = PrestamosAdapter(dbHelper.obtenerPrestamos())
        recyclerView.adapter = prestamosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}