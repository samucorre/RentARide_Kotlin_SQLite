package pf.dam.socios

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.SociosSQLite
import pf.dam.R
import pf.dam.SociosAdapter

class SociosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sociosAdapter: SociosAdapter
    private lateinit var dbHelper: SociosSQLite


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socios)

        dbHelper = SociosSQLite(this)
        recyclerView = findViewById(R.id.sociosRecyclerView)

        sociosAdapter = SociosAdapter(dbHelper.obtenerSocios())
        recyclerView.adapter = sociosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


    }
}