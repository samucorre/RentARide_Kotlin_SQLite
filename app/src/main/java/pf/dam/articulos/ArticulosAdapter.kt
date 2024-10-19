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
import androidx.core.graphics.green
import androidx.core.graphics.red
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

        if (articulo.rutaImagen != null) {
            val imagenBitmap = BitmapFactory.decodeFile(articulo.rutaImagen)
            holder.imagenImageView.setImageBitmap(imagenBitmap)
        } else {
            // Mostrar una imagen por defecto si no hay rutaImagen
            holder.imagenImageView.setImageResource(R.drawable.ico_imagen)
        }


            val cardView = holder.itemView.findViewById<CardView>(R.id.cardView)
            val context = holder.itemView.context
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    when (articulo.estado) {
                        EstadoArticulo.DISPONIBLE -> R.color.dispo // Color para estado disponible
                        EstadoArticulo.PRESTADO -> R.color.grey // Color para estado prestado
                        EstadoArticulo.NO_DISPONIBLE -> R.color.no_dispo // Color para estado no disponible
                        else -> R.color.yellowRRR // Color por defecto
                    }
                )
            )


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