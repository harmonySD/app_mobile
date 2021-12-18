package m1.pmob.veget_eau

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import m1.pmob.veget_eau.databinding.FragmentFicheBinding

class FicheFragment : Fragment(R.layout.fragment_fiche) {
    // Ce Fragment sert à représenter la fiche complète d'une plante à arroser
    // idée rajouter ici
    // - la date du prochain arrosage
    // - le prochain arrosage tel que défini par l'utilisateur (s'il a fait "Me rappeler plus tard")
    // un bouton pour set 1 date particulière qui ne respecte pas le calendrier ?
    companion object {
        @JvmStatic
        fun newInstance()=FicheFragment()
    }

    private lateinit var  binding : FragmentFicheBinding
    lateinit var model : MyViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)//permet de survivre aux rotations d'écrans ...
        binding = FragmentFicheBinding.bind(view)
        model = ViewModelProvider(this).get(MyViewModel::class.java)
        var p: TextView = binding.plantePrincipale
        var p2: TextView =binding.planteSecondaire
        var photo: ImageView =binding.imageplante
        val n=activity?.intent?.getLongExtra("plante",-1) // on récupère l id de la plante que l'utilisateur voulait charger
        //Log.d("uRI", "n ${n}")

        var pl= n?.let { oskour(it.toLong()) }
       if (pl != null) { // si on a bien trouver la plante que l'utilisateur voulait charger
           Log.d("uRI", "iciiiiii")
           try{ // tentative de chargement standard de la photo
               val bmp:Bitmap =  BitmapFactory.decodeFile(pl.uri)
               photo.setImageBitmap(bmp)

           } catch(np : NullPointerException){// si la photo est introuvable
               photo.setImageDrawable(resources.getDrawable( R.drawable.tokenplant))
           }
           //continuer avec les frequences etc
           if(pl.nomverna=="non communiqué"){
               p.text=pl.nomscient
               p2.text=pl.nomverna
           }else{
               p.text = pl.nomverna
               p2.text=pl.nomscient
           }




       }
    }
    fun oskour(n: Long):Eplante{
        //peut etre mieux avec id pas string
        var p= Eplante(0, "", "", "")
       var thread = Thread{
           p = (model.dao.loadExactName(n))
       }
        thread.start()
        thread.join()
        return p
    }
}