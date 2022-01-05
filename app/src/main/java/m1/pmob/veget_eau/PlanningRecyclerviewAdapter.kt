package m1.pmob.veget_eau

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import m1.pmob.veget_eau.databinding.ItemLayoutBinding

class PlanningRecyclerviewAdapter(val colors: MutableList<String>): RecyclerView.Adapter<PlanningRecyclerviewAdapter.VH>() {
        //private var allPlantes: List<Eplante> = listOf()
        val checked = ArrayList<String>()

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

    var listener =View.OnClickListener { view ->
        val w=view as CheckedTextView
        w.toggle()
        if (w.isChecked){
            checked.add(w.text.toString())
        }else{
            checked.remove(w.text.toString())
        }
    }
    fun removeChecked() { colors.removeAll(checked)
        checked.clear()
        notifyDataSetChanged()
    }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningRecyclerviewAdapter.VH {
            //créer View d'un élément de la liste à partir de fichier layout xml
            val v = LayoutInflater
                .from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_checked, parent, false)

            /*installer le listener sur chaque View */
            v.setOnClickListener(listener)

            //créer et retourner le ViewHolder
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            /* recuperer la View :
     * holder.itemView c'est la View associée à ce holder */
            val checkedTextView = holder.itemView as CheckedTextView

            /* mettre la valeur colors[position] dans la View */
            checkedTextView.text = colors[position]

            /* mettre à jour la propriété checked de la View */
            checkedTextView.isChecked = checked.contains(colors[position])

        }


        override fun getItemCount(): Int = colors.size


    }