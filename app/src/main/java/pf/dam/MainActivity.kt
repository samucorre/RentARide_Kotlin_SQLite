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
//        /*if (dbHelperPrestamos.obtenerPrestamos().isEmpty()) {
//            insertarPrestamos(dbHelperPrestamos)
//        }

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
            Articulo(1, "Bicicleta", "Tipo", "nome1", "Descripcion", EstadoArticulo.DISPONIBLE, "ruta/imagen1.jpg"),
            Articulo(2, "Kayak", "Tipo", "nome2", "Descripcion", EstadoArticulo.DISPONIBLE, "ruta/imagen2.jpg"),
            Articulo(3, "Bicicleta", "Tipo", "nome3", "Descripcion", EstadoArticulo.DISPONIBLE, "ruta/imagen1.jpg"),
            Articulo(4, "Kayak", "Tipo", "nome4", "Descripcion", EstadoArticulo.DISPONIBLE, "ruta/imagen2.jpg"),
//            Articulo(5, "Bicicleta", "Tipo", "nome5", "Descripcion", EstadoArticulo.DISPONIBLE, "ruta/imagen1.jpg"),
//            Articulo(6, "Kayak", "Tipo", "nome6", "Descripcion", EstadoArticulo.DISPONIBLE, "ruta/imagen2.jpg"),
//            Articulo(7, "Bicicleta", "Tipo", "nome7", "Descripcion", EstadoArticulo.DISPONIBLE, "ruta/imagen1.jpg"),
//            Articulo(8, "Kayak", "Tipo", "nome8", "Descripcionual", EstadoArticulo.DISPONIBLE, "ruta/imagen2.jpg"),
//            Articulo(9, "Bicicleta", "Tipo", "nome9", "BicDescripcionfantil", EstadoArticulo.DISPONIBLE, "ruta/imagen1.jpg"),
//            Articulo(10, "Kayak", "Tipo", "nome10", "InDescripcional", EstadoArticulo.DISPONIBLE, "ruta/imagen2.jpg"),


        )

        for (articulo in articulos) {
            dbHelperArticulos.insertarArticulo(articulo)
        }
    }
    private fun insertarSocios(dbHelperSocios: SociosSQLite) {
        val socios = listOf(
            Socio(1, "Juan", "Pérez", 1234, 654321098, "juan.perez@example.com"),
            Socio(2, "María", "García", 5678, 678901234, "maria.garcia@example.com"),
            // Agrega más socios aquí...
        )

        for (socio in socios) {
            dbHelperSocios.insertarSocio(socio)
        }
    }
}