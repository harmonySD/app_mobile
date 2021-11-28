package m1.pmob.veget_eau

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import m1.pmob.veget_eau.databinding.ItemLayoutBinding

class MyRecAdapter():RecyclerView.Adapter<MyRecAdapter.VH>() {
    private var allPlantes: List<Eplante> = listOf()

    class VH(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var plantes: Eplante
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecAdapter.VH {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = VH(binding)
        holder.itemView.setOnClickListener {v->
            val iii = Intent(v.context, FicheActivity::class.java)
            val position= holder.absoluteAdapterPosition
            Log.d("TOUCHER OK",position.toString())
            v.context.startActivity(iii)

        }
        return holder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.d("ici", "$position")
        var p: TextView = holder.itemView.findViewById(R.id.nomscient)
        var c: TextView = holder.itemView.findViewById(R.id.nomverna)
        //var sup: TextView=holder.itemView.findViewById(R.id.superficie)
        p.text = allPlantes[position].nomscient
        //Log.d(TAG,"${p.text}")
        c.text = allPlantes[position].nomverna
        //continent.text=allPays[position].continent
        //sup.text=sortedList[position].superficie
        holder.itemView.setBackgroundColor(
            if (position % 2 == 0)
                Color.argb(30, 0, 220, 0)
            else
                Color.argb(30, 0, 0, 220)
        )
    }

    override fun getItemCount(): Int = allPlantes.size

    fun setPlantes(plante: List<Eplante>?) {
        if (plante != null) {
            allPlantes = plante
            notifyDataSetChanged()
            Log.d("adapter", "la ici,${allPlantes.size}")
        } else {
            Log.d("adapter", "ici")
        }
    }
}