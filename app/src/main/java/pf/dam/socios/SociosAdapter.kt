package pf.dam.socios

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import db.PrestamosSQLite
import pf.dam.R
import java.text.SimpleDateFormat
import java.util.Locale

class SociosAdapter(socios: List<Socio>) :
    RecyclerView.Adapter<SociosAdapter.SocioViewHolder>() {

        var socios : List<Socio> = socios
                set(value) {
                    field = value
                    notifyDataSetChanged()
    }
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    class SocioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        val numeroSocioTextView: TextView = itemView.findViewById(R.id.numeroSocioTextView)
        val telefonoTextView: TextView = itemView.findViewById(R.id.telefonoTextView)
        val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val fechaNacimientoTextView: TextView = itemView.findViewById(R.id.fechaNacimientoTextView)
        val fechaIngresoSocioTextView: TextView = itemView.findViewById(R.id.fechaIngresoSocioTextView)
        val generoTextView: TextView = itemView.findViewById(R.id.generoTextView)
        val iconoPrestamoActivo: ImageView = itemView.findViewById(R.id.iconoPrestamoActivo)
        val dbPrestamos = PrestamosSQLite(itemView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.socio_item, parent, false)
        return SocioViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socio = socios[position]
        holder.nombreTextView.text = "${socio.nombre} ${socio.apellido}"
        holder.numeroSocioTextView.text = "Número de socio: ${socio.numeroSocio}"
        holder.telefonoTextView.text = "Teléfono: ${socio.telefono}"
        holder.emailTextView.text = "Email: ${socio.email}"
        holder.fechaNacimientoTextView.text = "Fecha de nacimiento: ${dateFormat.format(socio.fechaNacimiento)}"
        holder.fechaIngresoSocioTextView.text = "Fecha de ingreso: ${dateFormat.format(socio.fechaIngresoSocio)}"
        holder.generoTextView.text = "Género: ${socio.genero}"
        val db = holder.dbPrestamos.readableDatabase
        val tienePrestamoActivo = socio.idSocio?.let { holder.dbPrestamos.estaSocioEnPrestamoActivo(it) }
        db.close()

        if (tienePrestamoActivo == true) {
            holder.iconoPrestamoActivo.visibility = View.VISIBLE
        } else {
            holder.iconoPrestamoActivo.visibility = View.GONE
        }

    holder.itemView.setOnClickListener {
        val context = holder.itemView.context
                val intent = Intent(context, SocioDetalleActivity::class.java)
        intent.putExtra("idSocio", socio.idSocio)
        context.startActivity(intent)
    }
}
    override fun getItemCount(): Int {
        return socios.size
    }
}