package m1.pmob.veget_eau

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import m1.pmob.veget_eau.databinding.FragmentConsulterBinding
import m1.pmob.veget_eau.databinding.FragmentFicheBinding

class FicheFragment : Fragment(R.layout.fragment_fiche) {

    companion object {
        @JvmStatic
        fun newInstance()=FicheFragment()
    }
    private lateinit var  binding : FragmentFicheBinding
    //lateinit var model : MyViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFicheBinding.bind(view)
        var p: TextView = binding.planteF
        val n=activity?.intent?.getStringExtra("plante")
        p.text = n
    }

}