package pf.dam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import pf.dam.prestamos.Prestamo
import java.text.SimpleDateFormat
import java.util.*

class PrestamosAdapter(private var prestamos: List<Prestamo>) :
    RecyclerView.Adapter<PrestamosAdapter.PrestamoViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    class PrestamoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idArticuloTextView: TextView = itemView.findViewById(R.id.idArticuloTextView)
        val idSocioTextView: TextView = itemView.findViewById(R.id.idSocioTextView)
        val fechaInicioTextView: TextView = itemView.findViewById(R.id.fechaInicioTextView)
        val fechaFinTextView: TextView = itemView.findViewById(R.id.fechaFinTextView)
        val infoTextView: TextView = itemView.findViewById(R.id.infoTextView)
        // ... otros TextViews o Views que necesites
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prestamo, parent, false) // Asegúrate de tener este layout
        return PrestamoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        val prestamo = prestamos[position]
        holder.idArticuloTextView.text = "ID Artículo: ${prestamo.idArticulo}"
        holder.idSocioTextView.text = "ID Socio: ${prestamo.idSocio}"
        holder.fechaInicioTextView.text = "Fecha Inicio: ${dateFormat.format(prestamo.fechaInicio)}"
        holder.fechaFinTextView.text = "Fecha Fin: ${dateFormat.format(prestamo.fechaFin)}"
        holder.infoTextView.text = "Info: ${prestamo.info}"
        // ... configura otros TextViews o Views
    }

    override fun getItemCount(): Int = prestamos.size
}