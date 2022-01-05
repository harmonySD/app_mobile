package m1.pmob.veget_eau

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import m1.pmob.veget_eau.databinding.ItemLayoutBinding

class PlanningRecyclerviewAdapter(): RecyclerView.Adapter<PlanningRecyclerviewAdapter.VH>() {
        private var allPlantes: List<Eplante> = listOf()

        class VH(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
            lateinit var plantes: Eplante
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningRecyclerviewAdapter.VH {
            val binding =
                ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val holder = VH(binding)
            holder.itemView.setOnClickListener { v ->
                val iii = Intent(v.context, FicheActivity::class.java)
                val position = holder.absoluteAdapterPosition
                iii.putExtra("plante", allPlantes[position].id)
                v.context.startActivity(iii)
            }
            return holder
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            var p: TextView = holder.itemView.findViewById(R.id.nomscient)
            var c: TextView = holder.itemView.findViewById(R.id.nomverna)
            p.text = allPlantes[position].nomscient
            c.text = allPlantes[position].nomverna
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
            } else {
            }
        }
    }