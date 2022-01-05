package m1.pmob.veget_eau

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    lateinit var model : PlanningViewModel
    lateinit var adapter: PlanningRecyclerviewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlanningBinding.bind(view)
        model = ViewModelProvider(this).get(PlanningViewModel::class.java)

        val recyclerView = binding.recyclerView
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(activity)
            //adapter = MyRecAdapter()
        adapter = PlanningRecyclerviewAdapter(model.plantsToWater)

        recyclerView.adapter = adapter
        //observer
       // model.plantsToWater.observe(viewLifecycleOwner){adapter.setPlantes(model.plantsToWater.value)}


        // ECOUTEURS POUR LES BOUTONS DES PLANTES
       // binding.SnoozeButton.setOnClickListener(){

        //}

//        binding.waterButton.setOnClickListener(){


      //  }
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


}