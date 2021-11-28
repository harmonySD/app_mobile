package m1.pmob.veget_eau

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import m1.pmob.veget_eau.databinding.ChoixFreqBinding
import m1.pmob.veget_eau.databinding.FragmentAjouterBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList


class AjouterFragment : Fragment(R.layout.fragment_ajouter) {

    companion object {
        @JvmStatic
        fun newInstance()=AjouterFragment()
    }

    private lateinit var binding : FragmentAjouterBinding
    val model by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAjouterBinding.bind(view)
        binding.bAjouter.setOnClickListener {
            var nc = binding.edNomverna.text.toString().trim()
            var ns = binding.edNomscient.text.toString().trim()
            val uri = binding.edUri.text.toString().trim()

            if (nc == "" && ns == "") {
                afficherDialog("mettre au moins un nom :(")
                return@setOnClickListener
            } else if (nc == "") {
                nc = "non communiqué"
            } else if (ns == "") {
                ns = "non communiqué"
            }

            if(!checkDates()){
                Toast.makeText(context,"Une date est  incorrecte !",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addPlanteAndArros(nc,ns,uri)

            binding.edNomscient.text.clear()
            binding.edNomverna.text.clear()
            binding.edUri.text.clear()
            clearAllArros()

        }
        //==================== PARTIE POUR LA FREQUENCE==================
        binding.arros1.chckactiv.setOnClickListener {
            activateArrosElem(binding.arros1,binding.arros1.chckactiv.isChecked)
        }

        binding.arros2.chckactiv.setOnClickListener {
            activateArrosElem(binding.arros2,binding.arros2.chckactiv.isChecked)
            }

        binding.arros3.chckactiv.setOnClickListener {
            activateArrosElem(binding.arros3,binding.arros3.chckactiv.isChecked)
        }
        // au début aucune fréquence n'est activée
        activateArrosElem(binding.arros1,false)
        activateArrosElem(binding.arros2,false)
        activateArrosElem(binding.arros3,false)
    }

    fun afficherDialog( s: String ){
        context?.let {
            AlertDialog.Builder(it)
                .setMessage( s )
                .setPositiveButton("OK"){ d, _ -> d.dismiss() }
                .show()
        }
        return
    }

    fun activateArrosElem(target:ChoixFreqBinding,newStatus:Boolean):Unit{ //permet d'activer ou de désactiver une fréquence d'arrosage
        target.jourdeb.isEnabled = newStatus
        target.moisdeb.isEnabled = newStatus
        target.jourfin.isEnabled = newStatus
        target.moisfin.isEnabled = newStatus
        target.radbnormal.isEnabled = newStatus
        target.radbnutri.isEnabled = newStatus
        target.edtextfreqj.isEnabled = newStatus
    }

    private fun addPlanteAndArros( nc:String, ns:String, uri:String){//ajoute la plante et les arrosages à la BD
        val tab = ArrayList<Earrosage>(0)
        for (e in arrayOf(  makeInexactArros(binding.arros1),
        makeInexactArros(binding.arros2),
        makeInexactArros(binding.arros3))){
            if(e != null){
                tab.add(e)
            }
        }

        model.addPlantesandArros(
            n = nc, ns = ns, uri = uri, *(tab.toTypedArray() )
        )
    }

    private fun makeInexactArros(target: ChoixFreqBinding):Earrosage?{ // créé une entite d'arrosage avec un idplante inconnu pour servir temporairement.
        //TODO cette fonction sera très probablement réutilisable/déplaçable autre part!
        if(!target.chckactiv.isChecked){return null} // si la fréquence n'est pas "activée", on retourne !
        val type =  if(target.radbnutri.isSelected ) Typearros.STANDARD else Typearros.NUTRITIF
        val DF = SimpleDateFormat("dd.MM.yyyy")
        val deb:Date = DF.parse((target.jourdeb.selectedItemPosition+1).toString()+"."+(target.moisdeb.selectedItemPosition+1).toString()+".2000")!!
        val fin:Date = DF.parse((target.jourfin.selectedItemPosition+1).toString()+"."+(target.moisfin.selectedItemPosition+1).toString()+".2000")!!

        return Earrosage(idp=0,type=type,deb=deb,fin=fin,interval=target.edtextfreqj.text.toString().toInt())
    }

    private fun checkDates():Boolean{ // vérifie que les dates respectent bien un schéma correct avant ajout
        val DF = SimpleDateFormat("dd.MM.yyyy")
        for(target in arrayOf(binding.arros1,binding.arros2,binding.arros3)){
            val  deb:Date? = DF.parse((target.jourdeb.selectedItemPosition+1).toString()+"."+(target.moisdeb.selectedItemPosition+1).toString()+".2000")
            val fin:Date? = DF.parse((target.jourfin.selectedItemPosition+1).toString()+"."+(target.moisfin.selectedItemPosition+1).toString()+".2000")
        if(deb == null||fin==null){
            return false
        }
    }
        return true
    }

    private fun clearAllArros(){ // vide les champs d'arros
        for (target in arrayOf(binding.arros1,binding.arros2,binding.arros3)){
            target.edtextfreqj.text.clear()
            target.jourdeb.setSelection(0);
            target.moisdeb.setSelection(0);
            target.jourfin.setSelection(0);
            target.moisfin.setSelection(0);
        }

    }
}