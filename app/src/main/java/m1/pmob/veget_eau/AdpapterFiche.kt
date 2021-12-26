package m1.pmob.veget_eau

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import m1.pmob.veget_eau.databinding.Item2LayoutBinding
import m1.pmob.veget_eau.databinding.ItemLayoutBinding

class AdpapterFiche (): RecyclerView.Adapter<AdpapterFiche.VH>() {
    private var allarros: List<Earrosage> = listOf()

    class VH(val binding: Item2LayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var arrosage: Earrosage
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdpapterFiche.VH {
        //val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding = Item2LayoutBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        val holder = VH(binding)
        return holder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.d("ici", "$position")

        var d: TextView = holder.itemView.findViewById(R.id.deb)
        var f: TextView = holder.itemView.findViewById(R.id.fin)
        var i: TextView = holder.itemView.findViewById(R.id.intervale)
        var t: TextView = holder.itemView.findViewById(R.id.type)
        //var sup: TextView=holder.itemView.findViewById(R.id.superficie)
        d.text = allarros[position].deb.toString()
        //Log.d(TAG,"${p.text}")
        f.text = allarros[position].fin.toString()
        i.text = allarros[position].interval.toString()
        t.text = allarros[position].type.toString()

        //continent.text=allPays[position].continent
        //sup.text=sortedList[position].superficie
        holder.itemView.setBackgroundColor(
            if (position % 2 == 0)
                Color.argb(30, 0, 220, 0)
            else
                Color.argb(30, 0, 0, 220)
        )
    }

    override fun getItemCount(): Int = allarros.size

    fun setArros(arrosage: List<Earrosage>?) {
        if (arrosage != null) {
            allarros = arrosage
            notifyDataSetChanged()
            //Log.d("adapter", "la ici,${allPlantes.size}")
        } else {
            Log.d("adapter", "ici")
        }
    }
}