package m1.pmob.veget_eau

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import m1.pmob.veget_eau.databinding.FragmentConsulterBinding
import m1.pmob.veget_eau.databinding.FragmentFicheBinding

class FicheFragment : Fragment(R.layout.fragment_fiche) {

    companion object {
        @JvmStatic
        fun newInstance()=FicheFragment()
    }
    //var planteFi =MutableLiveData<Eplante>()
    private lateinit var  binding : FragmentFicheBinding
    lateinit var model : MyViewModel
   // lateinit var planteFiche: Eplante
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFicheBinding.bind(view)
        model = ViewModelProvider(this).get(MyViewModel::class.java)
        var p: TextView = binding.planteF
        var photo: ImageView =binding.imageplante
        val n=activity?.intent?.getStringExtra("plante")
        p.text = n
        Log.d("uRI", "n ${n}")
        //model.getPlanteByName(n)
        //oskour(n)
        //var planteFiche= planteFi.value

       Thread{
          var  planteFi=(model.dao.loadExactName(n))
           Log.d("uRI", "la ${Uri.parse(planteFi.uri)}")
           //photo.setImageURI(Uri.parse(planteFi.uri))
       }.start()

        //affiche juste la
        //hyp uri non stocker
//        Log.d("uRI", "la ${Uri.parse(planteFiche?.uri)}")
      //  Log.d("uRI", "la ${planteFiche?.uri}")
        //Log.d("uRI", "nom ${planteFiche?.nomverna}")
    photo.setImageURI(Uri.parse("content://media/external/images/media/193094"))


    }


}