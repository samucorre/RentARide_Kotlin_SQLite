package pf.dam

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.articulos.ArticulosActivity
import pf.dam.prestamos.PrestamosActivity
import pf.dam.socios.SociosActivity
import pf.dam.utils.InsertarDatosIniciales
import pf.dam.utils.graficos.GraficosActivity
import pf.dam.utils.exportBd.ExportActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbArticulos: ArticulosSQLite
    private lateinit var dbSocios: SociosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbArticulos = ArticulosSQLite(this)
        dbSocios = SociosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)

        if (dbSocios.getAllSocios().isEmpty()) {
            InsertarDatosIniciales().insertarSociosIniciales(dbSocios)
        }
        if (dbArticulos.getAllArticulos().isEmpty()) {
            InsertarDatosIniciales().insertarArticulosIniciales(dbArticulos)
        }
        if (dbPrestamos.getAllPrestamos().isEmpty()) {
            InsertarDatosIniciales().insertarPrestamosIniciales(dbPrestamos)
        }

        val cardView1 = findViewById<CardView>(R.id.cardView1)
        val cardView2 = findViewById<CardView>(R.id.cardView2)
        val cardView3 = findViewById<CardView>(R.id.cardView3)
        val cardView4 = findViewById<CardView>(R.id.cardView4)
        val cardView5 = findViewById<CardView>(R.id.cardView5)

        cardView1.setOnClickListener {
            val intent = Intent(this, ArticulosActivity::class.java)
            startActivity(intent)
        }
        cardView2.setOnClickListener {
            val intent = Intent(this, SociosActivity::class.java)
            startActivity(intent)
        }
        cardView3.setOnClickListener {
            val intent = Intent(this, PrestamosActivity::class.java)
            startActivity(intent)
        }
        cardView4.setOnClickListener {
            val intent = Intent(this, GraficosActivity::class.java)
            startActivity(intent)
        }
        cardView5.setOnClickListener {
            val intent = Intent(this, ExportActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_close, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cerrar_app -> {
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}