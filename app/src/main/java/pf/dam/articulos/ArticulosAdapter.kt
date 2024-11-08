package pf.dam.articulos

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat


import androidx.recyclerview.widget.RecyclerView
import db.ArticulosSQLite
import db.SociosSQLite
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
        //val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagenImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_articulo, parent, false)
        return ArticuloViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticuloViewHolder, position: Int) {
        val articulo = articulos[position]
        holder.nombreTextView.text = articulo.nombre ?: ""
        holder.categoriaTextView.text = articulo.categoria ?: ""
        holder.tipoTextView.text = articulo.tipo ?: ""
        holder.descripcionTextView.text = articulo.descripcion ?: ""
      //  holder.estadoTextView.text = articulo.estado?.name ?: ""
        if (articulo.rutaImagen.isNullOrEmpty()) {
            holder.imagenImageView.setImageResource(R.drawable.ico_imagen)
        } else {
            // Mostrar una imagen por defecto si no hay rutaImagen
            val imagenBitmap = BitmapFactory.decodeFile(articulo.rutaImagen)
            holder.imagenImageView.setImageBitmap(imagenBitmap)
        }
        val iconoEstado = holder.itemView.findViewById<ImageView>(R.id.iconoEstado)
        val cardView = holder.itemView.findViewById<CardView>(R.id.cardView)
        val context = holder.itemView.context

        when (articulo.estado) {
            EstadoArticulo.DISPONIBLE -> {
                iconoEstado.setImageResource(R.drawable.ico_dispo)
                iconoEstado.visibility = View.VISIBLE
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dispo))
            }
            EstadoArticulo.PRESTADO -> {
                iconoEstado.setImageResource(R.drawable.ico_prestado)
                iconoEstado.visibility = View.VISIBLE
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey))
            }
            EstadoArticulo.NO_DISPONIBLE -> {
                iconoEstado.setImageResource(R.drawable.ico_nodispo)
                iconoEstado.visibility = View.VISIBLE
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.no_dispo))
            }
            else -> {
                iconoEstado.visibility = View.GONE
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.yellowRRR))
            }
        }




        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val dbHelper = ArticulosSQLite(context)
            val idArticulo = dbHelper.obtenerIdArticuloBD(articulo)
            val intent = Intent(context, ArticuloDetalleActivity::class.java)
            intent.putExtra("idArticulo", idArticulo)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return articulos.size
    }
}