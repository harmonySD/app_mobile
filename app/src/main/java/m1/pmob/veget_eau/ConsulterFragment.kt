package m1.pmob.veget_eau

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import m1.pmob.veget_eau.databinding.FragmentConsulterBinding


class ConsulterFragment : Fragment(R.layout.fragment_consulter) {

    companion object {
        @JvmStatic
        fun newInstance()=ConsulterFragment()
    }
    private lateinit var  binding : FragmentConsulterBinding
    lateinit var model : MyViewModel
    lateinit var adapter: MyRecAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentConsulterBinding.bind(view)
        model = ViewModelProvider(this).get(MyViewModel::class.java)
        val recyclerView = binding.recyclerView
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = MyRecAdapter()
        recyclerView.adapter = adapter
        //observer
        model.certainesPlantes.observe(viewLifecycleOwner){adapter.setPlantes(it)}
        model.plantes.observe(viewLifecycleOwner){adapter.setPlantes(model.plantes.value)}

        binding.plantes.addTextChangedListener(object : TextWatcher{
            //val TAG: String = "pays watcher"
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                //Log.d(TAG, "entre")
                if (p0 == null) {
                    return
                }
                val pr = p0.toString().trim()
                model.getPlantesPrefix(pr)
                //Log.d(TAG, "qqc $pr")
            }
        })


    }


}