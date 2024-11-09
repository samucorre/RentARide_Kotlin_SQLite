package pf.dam

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import db.PrestamosSQLite
import pf.dam.R.color.yellowRRR
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.Prestamo
import pf.dam.prestamos.PrestamoDetalleActivity
import java.text.SimpleDateFormat
import java.util.*

class PrestamosAdapter(prestamos: List<Prestamo>) : RecyclerView.Adapter<PrestamosAdapter.PrestamoViewHolder>() {
    var prestamos: List<Prestamo> = prestamos
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    class PrestamoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idPrestamoTextView: TextView = itemView.findViewById(R.id.idPrestamoTextView)
        val idArticuloTextView: TextView = itemView.findViewById(R.id.idArticuloTextView)
        val categoriaArticuloTextView: TextView = itemView.findViewById(R.id.categoriaArticuloTextView)
        val imagenArticuloImageView: ImageView = itemView.findViewById(R.id.imagenArticuloImageView)
        val datosSocioTextView: TextView = itemView.findViewById(R.id.datosSocioTextView)
        val datos1SocioTextView: TextView = itemView.findViewById(R.id.datos1SocioTextView)
        val datos2SocioTextView: TextView = itemView.findViewById(R.id.datos2SocioTextView)
        val fechaInicioTextView: TextView = itemView.findViewById(R.id.fechaInicioTextView)
        val fechaFinTextView: TextView = itemView.findViewById(R.id.fechaFinTextView)
        val infoTextView: TextView = itemView.findViewById(R.id.infoTextView)
        val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        //val nombreArticuloTextView: TextView = itemView.findViewById(R.id.nombreArticuloTextView) // Nuevo TextView para el nombre del artículo
      //  val datosSocioTextView: TextView = itemView.findViewById(R.id.datosSocioTextView) // Nuevo TextView para los datos del socio
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prestamo, parent, false) // Asegúrate de tener este layout
        return PrestamoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        val prestamo = prestamos[position]
        holder.idPrestamoTextView.text = "ID Préstamo: ${prestamo.idPrestamo}"
        holder.idArticuloTextView.text = "ID Art: ${prestamo.idArticulo}"
        holder.datosSocioTextView.text = "ID Soc: ${prestamo.idSocio}"
        holder.fechaInicioTextView.text = "Fecha Inicio: ${dateFormat.format(prestamo.fechaInicio)}"
        val fechaFinString = if (prestamo.fechaFin != null) {
            dateFormat.format(prestamo.fechaFin)
        } else {
            "" // O "" para dejarlo vacío
        }
        holder.fechaFinTextView.text = "Fecha Fin: $fechaFinString"
        holder.infoTextView.text = "Info: ${prestamo.info}"
        holder.estadoTextView.text = "Estado: ${prestamo.estado}"

        val context = holder.itemView.context
        holder.estadoTextView.setBackgroundColor(
            ContextCompat.getColor(
                context,
                when (prestamo.estado) {
                    EstadoPrestamo.ACTIVO -> {
                        R.color.dispo
                    }
                    EstadoPrestamo.CERRADO -> {
                        yellowRRR }

                    else -> {
                        R.color.black
                    }
                }
            )
        )

        // Obtener los datos del artículo y del socio
        val dbHelper = PrestamosSQLite(holder.itemView.context)
        val articulo = dbHelper.obtenerArticuloPrestamoId(prestamo.idArticulo)
        val socio = dbHelper.obtenerSocioPrestamoId(prestamo.idSocio)
        val imagenBitmap = BitmapFactory.decodeFile(articulo?.rutaImagen ?: "")

        // Mostrar los datos en los TextViews
        if (articulo != null) {
            holder.idArticuloTextView.text = "${articulo.nombre} "
            holder.categoriaArticuloTextView.text = "${articulo.categoria}"
            holder.imagenArticuloImageView.setImageBitmap(imagenBitmap)

        }
        if (socio != null) {
            holder.datosSocioTextView.text = "Socio: ${socio.nombre} ${socio.apellido} "
            holder.datos1SocioTextView.text ="Nº Socio: ${socio.numeroSocio}"
            holder.datos2SocioTextView.text = "Teléfono: ${socio.telefono}"
            // ... (muestra los demás datos del socio si es necesario) ...
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PrestamoDetalleActivity::class.java)

            intent.putExtra("idPrestamo", prestamo.idPrestamo)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = prestamos.size
}
