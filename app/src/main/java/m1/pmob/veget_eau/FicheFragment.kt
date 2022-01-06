package m1.pmob.veget_eau

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import m1.pmob.veget_eau.databinding.FragmentFicheBinding

class FicheFragment : Fragment(R.layout.fragment_fiche) {
    // Ce Fragment sert à représenter la fiche complète d'une plante à arroser
    companion object {
        @JvmStatic
        fun newInstance()=FicheFragment()
    }

    private lateinit var  binding : FragmentFicheBinding
    lateinit var model : MyViewModel
    lateinit var adapter: AdpapterFiche
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)//permet de survivre aux rotations d'écrans ...
        binding = FragmentFicheBinding.bind(view)
        model = ViewModelProvider(this).get(MyViewModel::class.java)
        adapter = AdpapterFiche()
        val recyclerView = binding.recyclerView
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        var p: TextView = binding.plantePrincipale
        var p2: TextView =binding.planteSecondaire
        var photo: ImageView =binding.imageplante
        var f: TextView = binding.freq
        val n=activity?.intent?.getLongExtra("plante",-1) // on récupère l id de la plante que l'utilisateur voulait charger

        var pl= n?.let { getPlante(it.toLong()) }
       if (pl != null) { // si on a bien trouver la plante que l'utilisateur voulait charger
           try{ // tentative de chargement standard de la photo
               val bmp:Bitmap =  BitmapFactory.decodeFile(pl.uri)
               photo.setImageBitmap(bmp)

           } catch(np : NullPointerException){// si la photo est introuvable
               photo.setImageDrawable(resources.getDrawable( R.drawable.tokenplant,null))
           }
           //affichage des noms avec tjs en premier un des noms communique
           if(pl.nomverna=="non communiqué"){
               p.text=pl.nomscient
               p2.text=pl.nomverna
           }else{
               p.text = pl.nomverna
               p2.text=pl.nomscient
           }
           //affichage des frequences
           model.loadAllArrosByID(pl.id)
           model.listeArros.observe(viewLifecycleOwner){adapter.setArros(it)}


       }
    }
    fun getPlante(n: Long):Eplante{
        //peut etre mieux avec id pas string
        var p= Eplante(0, "", "", "")
       var thread = Thread{
           p = (model.dao.loadPlanteByID(n))
       }
        thread.start()
        thread.join()
        return p
    }

}