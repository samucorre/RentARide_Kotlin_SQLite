package pf.dam.articulos

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import db.ArticulosSQLite
import pf.dam.R

class ArticulosAdapter(articulos: List<Articulo>) :
    RecyclerView.Adapter<ArticulosAdapter.ArticuloViewHolder>() {

    var articulos: List<Articulo> = articulos
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ArticuloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        val categoriaTextView: TextView = itemView.findViewById(R.id.categoriaTextView)
        val tipoTextView: TextView = itemView.findViewById(R.id.tipoTextView)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
        val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_articulo, parent, false)
        return ArticuloViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticuloViewHolder, position: Int) {
        val articulo = articulos[position]
        holder.nombreTextView.text = articulo.nombre
        holder.categoriaTextView.text = articulo.categoria
        holder.tipoTextView.text = articulo.tipo
        holder.descripcionTextView.text = articulo.descripcion
        holder.estadoTextView.text = articulo.estado

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val dbHelper = ArticulosSQLite(context) // Obtener una instancia de tu helper de base de datos
            val idArticulo = dbHelper.obtenerIdArticuloBD(articulo)  // Obtener el ID del artículo por su nombre
            val intent = Intent(context, ArticuloDetalleActivity::class.java)
            intent.putExtra("idArticulo", idArticulo)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articulos.size
    }
}