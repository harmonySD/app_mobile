package m1.pmob.veget_eau

import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Job
import m1.pmob.veget_eau.databinding.FragmentConsulterBinding
import m1.pmob.veget_eau.databinding.FragmentFicheBinding
import java.io.File

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
        var pl= n?.let { oskour(it) }
       if (pl != null) {
            if(pl.uri !="null"){
               Log.d("uRI", " sans parse ${pl.uri}")
               Log.d("uRI", "avec ${Uri.parse(pl.uri)}")
               val bmp:Bitmap = BitmapFactory.decodeFile(pl.uri)
              // decodeStream(view.context.contentResolver.openInputStream(pl.uri)))
                photo.setImageBitmap(bmp)
                }

            //photo.setImageURI(Uri.parse(pl.uri))
       }
        //var planteFiche= planteFi.value



        //affiche juste la
        //hyp uri non stocker
//        Log.d("uRI", "la ${Uri.parse(planteFiche?.uri)}")
      //  Log.d("uRI", "la ${planteFiche?.uri}")
        //Log.d("uRI", "nom ${planteFiche?.nomverna}")
   // photo.setImageURI(Uri.parse("content://media/external/images/media/193094"))


    }
    fun oskour(n:String):Eplante{
        var p= Eplante(0, "", "", "")
       var thread = Thread{
           p = (model.dao.loadExactName(n))
           Log.d("uRI", "la ${Uri.parse(p.uri)}")
           //photo.setImageURI(Uri.parse(planteFi.uri))
       }
        thread.start();
        thread.join()




        return p
    }


}