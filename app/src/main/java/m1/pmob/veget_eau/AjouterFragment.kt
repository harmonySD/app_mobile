package m1.pmob.veget_eau

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import m1.pmob.veget_eau.databinding.FragmentAjouterBinding


class AjouterFragment : Fragment(R.layout.fragment_ajouter) {

    companion object {
        @JvmStatic
        fun newInstance()=AjouterFragment()
    }

    private lateinit var binding : FragmentAjouterBinding
    val model by lazy{ ViewModelProvider(this).get(MyViewModel::class.java)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAjouterBinding.bind(view)

        binding.bAjouter.setOnClickListener{
            val nc = binding.edNomverna.text.toString().trim()
            val ns = binding.edNomscient.text.toString().trim()
            val uri = binding.edUri.text.toString().trim()
            model.addPlantes(n=nc,ns=ns,uri=uri)
            binding.edNomscient.text.clear()
            binding.edNomverna.text.clear()
            binding.edUri.text.clear()
            Log.d("kk","enregister")

        }
    }

}