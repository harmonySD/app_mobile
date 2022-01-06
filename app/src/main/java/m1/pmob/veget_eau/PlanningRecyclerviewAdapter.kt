package m1.pmob.veget_eau


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import m1.pmob.veget_eau.databinding.PlanningItemBinding


class PlanningRecyclerviewAdapter(
    val plannvm: PlanningViewModel, // on aura besoin du viewmodel pour envoyer des requêtes à la bd
    val tst: PlanningFragment, // on en a besoin pour observer le livedata
) : RecyclerView.Adapter<PlanningRecyclerviewAdapter.VH>() {
    val checked = ArrayList<Long>() // on stocke tous les id des plante
    private var colors: List<Eplante> = listOf()// un mauvais nom pour le mutable live data
    fun setPlantes(plante: List<Eplante>?) {
        if (plante != null) {
            colors = plante
            notifyDataSetChanged()
        } else {
        }
    }
    init {
        // on veut que lorsque le liveData change le viewmodel change
        // SUREMENT LA SOURCE DU PROBLEME
        // n'est pas remis à jour ...
       /* colors.observe(tst.viewLifecycleOwner) {
            Log.i("PLANNFRAG", " changement de bd !")
            this.notifyDataSetChanged()
            Log.i("PLANNFRAG", " notify fait")
            Log.i("PLANNFRAG", "comptage des elements :" + this.itemCount)
        }*/
    }

    class VH(bind: PlanningItemBinding) : RecyclerView.ViewHolder(bind.root) {
        val vhBinding: PlanningItemBinding = bind // pour éviter d'avoir à inflate à chaque modif
        lateinit var data: Eplante // on stocke directement dans le VH la plante à afficher

        fun bindPlante(plnt: Eplante) { // appelé à chaque onBindViewHolder
            data = plnt
            vhBinding.idplante.text = data.id.toString()
            vhBinding.nomplann.text = data.nomverna
            vhBinding.nomplannscient.text = data.nomscient
        }
    }

    fun removeChecked() { // pour dire qu'une plante a été arrosée.
        for (e in checked) {
            plannvm.setWatered(e)
        }
        checked.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlanningRecyclerviewAdapter.VH {
        //val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding =
            PlanningItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = PlanningRecyclerviewAdapter.VH(binding)


        binding.chkbox.setOnClickListener() {
            it as CheckBox
            if (it.isChecked) {
                checked.remove(holder.data.id)
            } else {
                checked.add(holder.data.id)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        // on fournit juste la plante au VH et il se débrouille
        holder.bindPlante(//colors.value!![position])
        colors[position] )

    }


    override fun getItemCount(): Int =
        try {
            colors.size
            //colors.value!!.size
        } catch (e: Exception) {
            //Log.i("PLANNFRAG", "comptage des elements :" + this.itemCount) // provoque des bugs si decommenté ??
            0
        }


}