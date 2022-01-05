package m1.pmob.veget_eau

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import m1.pmob.veget_eau.databinding.PlanningItemBinding


class PlanningRecyclerviewAdapter(val colors: MutableLiveData<MutableList<Eplante>>): RecyclerView.Adapter<PlanningRecyclerviewAdapter.VH>() {
        val checked = ArrayList<Int>()

    class VH(b: PlanningItemBinding) : RecyclerView.ViewHolder(b.root){
    }

    fun removeChecked() {
        checked.clear()
        notifyDataSetChanged()
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningRecyclerviewAdapter.VH {
            //créer View d'un élément de la liste à partir de fichier layout xml
            //val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val binding = PlanningItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            val holder = PlanningRecyclerviewAdapter.VH(binding)
            binding.root.setOnClickListener(){
                val binded = PlanningItemBinding.bind(it)
                if(binded.chkbox.isChecked){
                    checked.remove( binded.idplante.text.toString().toInt())
                    binded.chkbox.isChecked = false
                }else{
                    checked.add( binded.idplante.text.toString().toInt())
                    binded.chkbox.isChecked = true
                }
            }

            /*
                val v = LayoutInflater
                .from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_checked, parent, false)

            /*installer le listener sur chaque View */
            v.setOnClickListener(listener)
            */
            //créer et retourner le ViewHolder




            return holder
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            /* recuperer la View :
     * holder.itemView c'est la View associée à ce holder */
            val binding = holder.itemView as PlanningItemBinding

            /* mettre la valeur colors[position] dans la View */
            binding.idplante.text = colors[position].id.toString()


        }


        override fun getItemCount(): Int = colors.size


    }