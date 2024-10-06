package pf.dam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.articulos.Articulo
import pf.dam.articulos.ArticulosActivity
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.PrestamosActivity
import pf.dam.socios.Socio
import pf.dam.socios.SociosActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelperArticulos: ArticulosSQLite
    private lateinit var dbHelperSocios: SociosSQLite
    private lateinit var dbHelperPrestamos: PrestamosSQLite
    private lateinit var verArticulosButton: Button
    private lateinit var verSociosButton: Button
    private lateinit var verPrestamosButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar dbHelpers
        dbHelperArticulos = ArticulosSQLite(this)
        dbHelperSocios = SociosSQLite(this)
        dbHelperPrestamos = PrestamosSQLite(this)

        // Inicializar botones
        verArticulosButton = findViewById(R.id.verArticulosButton)
        verSociosButton = findViewById(R.id.verSociosButton)
        verPrestamosButton = findViewById(R.id.verPrestamosButton)

       // Insertar datos
        if (dbHelperArticulos.obtenerArticulos().isEmpty()) {
            insertarArticulos(dbHelperArticulos)
        }

        if (dbHelperSocios.obtenerSocios().isEmpty()) {
            insertarSocios(dbHelperSocios)
        }
        /*if (dbHelperPrestamos.obtenerPrestamos().isEmpty()) {
            insertarPrestamos(dbHelperPrestamos)
        }
*/
        if (dbHelperArticulos.obtenerArticulos().isEmpty()) {
            Toast.makeText(this, "No hay artículos", Toast.LENGTH_SHORT).show()
        }
        if (dbHelperSocios.obtenerSocios().isEmpty()) {
            Toast.makeText(this, "No hay socios", Toast.LENGTH_SHORT).show()
        }
        if (dbHelperPrestamos.obtenerPrestamos().isEmpty()) {
            Toast.makeText(this, "No hay préstamos", Toast.LENGTH_SHORT).show()
        }
        // Configurar listeners de botones
        verArticulosButton.setOnClickListener {
            val intent = Intent(this, ArticulosActivity::class.java)
            startActivity(intent)
        }

        verSociosButton.setOnClickListener {
            val intent = Intent(this, SociosActivity::class.java)
            startActivity(intent)
        }

        verPrestamosButton.setOnClickListener {
            val intent = Intent(this, PrestamosActivity::class.java)
            startActivity(intent)
        }
    }
    private fun insertarArticulos(dbHelperArticulos: ArticulosSQLite) {
        val articulos = listOf(
            Articulo(1, "Bicicleta", "MTB", "Bicicleta infantil", "Rueda 20", EstadoArticulo.DISPONIBLE, "ruta/imagen1.jpg"),
            Articulo(2, "Kayak", "Hinchable", "Ocean", "Individual", EstadoArticulo.DISPONIBLE, "ruta/imagen2.jpg"),
            Articulo(3, "Kayak", "Rigido", "River", "Dos plazas", EstadoArticulo.DISPONIBLE, "ruta/imagen3.jpg")
            // ... más artículos
        )

        for (articulo in articulos) {
            dbHelperArticulos.insertarArticulo(articulo)
        }
    }
    private fun insertarSocios(dbHelperSocios: SociosSQLite) {
        val socios = listOf(
            Socio(1,"Juan", "Pérez", 1234, 654321098, "juan.perez@example.com"),
            Socio(2,"María", "García", 5678, 612345678, "maria.garcia@example.com")
            // ... más socios
        )

        for (socio in socios) {
            dbHelperSocios.insertarSocio(socio)
        }
    }
/*
    private fun insertarPrestamos(dbHelperPrestamos: PrestamosSQLite) {
        val prestamos = listOf(
            Prestamo(1, 1234, Date(), Date(), "Préstamo de camiseta"),
            Prestamo(2, 5678, Date(), Date(), "Préstamo de pantalón")
            // ... más préstamos
        )

        for (prestamo in prestamos) {
            dbHelperPrestamos.insertarPrestamo(prestamo)
        }
    }*/
}