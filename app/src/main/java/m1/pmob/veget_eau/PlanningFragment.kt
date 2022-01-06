package m1.pmob.veget_eau

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import m1.pmob.veget_eau.databinding.FragmentConsulterBinding
import m1.pmob.veget_eau.databinding.FragmentPlanningBinding
import m1.pmob.veget_eau.databinding.PlanningItemBinding

class PlanningFragment : Fragment(R.layout.fragment_planning) {

    companion object {
        @JvmStatic
        fun newInstance() = PlanningFragment()
    }
    private lateinit var  binding : FragmentPlanningBinding
    lateinit var model : PlanningViewModel // le viewmodel pour les accès à la BD
    lateinit var adapter: PlanningRecyclerviewAdapter //l'adapteur du recyclerview


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

            binding = FragmentPlanningBinding.bind(view)
            model = ViewModelProvider(this).get(PlanningViewModel::class.java)

            val recyclerView = binding.recyclerView // on va faire plusieures opérations sur le recyclerview
            recyclerView.hasFixedSize()
            recyclerView.layoutManager = LinearLayoutManager(activity)
            adapter = PlanningRecyclerviewAdapter(model, this)
       // model.plantsToWater.observe(viewLifecycleOwner){adapter.setPlantes(it)}
        model.plantsToWater.observe(viewLifecycleOwner){adapter.setPlantes(model.plantsToWater.value)}
            recyclerView.adapter = adapter

         //ECOUTEURS POUR LES BOUTONS DES PLANTES
       binding.SnoozeButton.setOnClickListener(){
           adapter.removeChecked()
        }

        binding.waterButton.setOnClickListener(){
          adapter.removeChecked()
        }

    /* pas de champs de recherche de texte pour le moment
        binding.plantes.addTextChangedListener(object : TextWatcher {
            //val TAG: String = "pays watcher"
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 == null) {
                    return
                }
                val pr = p0.toString().trim()
                model.getPlantesPrefix(pr)
            }
        })
        */

    }
    fun arros( v: View){
        for(i in adapter.checked){
            model.setWatered(i)
        }
    }


}