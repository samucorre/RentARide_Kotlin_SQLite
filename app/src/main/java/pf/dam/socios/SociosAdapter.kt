package pf.dam.socios

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import db.SociosSQLite
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
        //val apellidoTextView: TextView = itemView.findViewById(R.id.apellidoTextView)
        val numeroSocioTextView: TextView = itemView.findViewById(R.id.numeroSocioTextView)
        val telefonoTextView: TextView = itemView.findViewById(R.id.telefonoTextView)
        val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val fechaNacimientoTextView: TextView = itemView.findViewById(R.id.fechaNacimientoTextView)
        val fechaIngresoSocioTextView: TextView = itemView.findViewById(R.id.fechaIngresoSocioTextView)
        val generoTextView: TextView = itemView.findViewById(R.id.generoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false) // Asegúrate de tener este layout
        return SocioViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socio = socios[position]
        holder.nombreTextView.text = "${socio.nombre} ${socio.apellido}"
        //holder.apellidoTextView.text = "Apellido: ${socio.apellido}"
        holder.numeroSocioTextView.text = "Número de socio: ${socio.numeroSocio}"
        holder.telefonoTextView.text = "Teléfono: ${socio.telefono}"
        holder.emailTextView.text = "Email: ${socio.email}"
        holder.fechaNacimientoTextView.text = "Fecha de nacimiento: ${dateFormat.format(socio.fechaNacimiento)}"
        holder.fechaIngresoSocioTextView.text = "Fecha de ingreso: ${dateFormat.format(socio.fechaIngresoSocio)}"
        holder.generoTextView.text = "Género: ${socio.genero}"

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