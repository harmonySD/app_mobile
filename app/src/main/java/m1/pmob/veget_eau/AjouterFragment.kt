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
import java.util.*


class AjouterFragment : Fragment(R.layout.fragment_ajouter) {

    companion object {
        @JvmStatic
        fun newInstance()=AjouterFragment()
    }

    private lateinit var binding : FragmentAjouterBinding
    val model by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAjouterBinding.bind(view)
        binding.bAjouter.setOnClickListener {
            val nc = binding.edNomverna.text.toString().trim()
            val ns = binding.edNomscient.text.toString().trim()
            val uri = binding.edUri.text.toString().trim()
            if (nc == "" && ns == "") {
                afficherDialog("mettre au moins un nom :(")
                return@setOnClickListener
            } else if (nc == "") {
                model.addPlantes(n = "non communiqué", ns = ns, uri = uri)
            } else if (ns == "") {
                model.addPlantes(n = nc, ns = "non communiqué", uri = uri)
            } else {
                model.addPlantes(n = nc, ns = ns, uri = uri)
            }
            binding.edNomscient.text.clear()
            binding.edNomverna.text.clear()
            binding.edUri.text.clear()

            addArros()
            Log.d("kk", "enregister")
        }
        //==================== PARTIE POUR LA FREQUENCE==================
        binding.arros1.chckactiv.setOnClickListener {
            alterArros(binding.arros1,binding.arros1.chckactiv.isChecked)
        }

        binding.arros2.chckactiv.setOnClickListener {
                alterArros(binding.arros2,binding.arros2.chckactiv.isChecked)
            }

        binding.arros3.chckactiv.setOnClickListener {
            alterArros(binding.arros3,binding.arros3.chckactiv.isChecked)
        }
        alterArros(binding.arros1,false)
        alterArros(binding.arros2,false)
        alterArros(binding.arros3,false)
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

    fun alterArros(target:ChoixFreqBinding,newStatus:Boolean):Unit{
        target.jourdeb.isEnabled = newStatus
        target.moisdeb.isEnabled = newStatus
        target.jourfin.isEnabled = newStatus
        target.moisfin.isEnabled = newStatus
        target.radbnormal.isEnabled = newStatus
        target.radbnutri.isEnabled = newStatus
        target.edtextfreqj.isEnabled = newStatus
    }

    fun addArros(target: ChoixFreqBinding,idp:Int,deb :Date,fin:Date){ //TODO cette fonction sera très probablement réutilisable autre part!
        if(!target.chckactiv.isChecked){return} // si la fréquence n'est pas cochée, on retourne !
        val type =  if(target.radbnutri.isSelected ) Typearros.STANDARD else Typearros.NUTRITIF

        val DF = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)
        val  deb:Date = DF.parse(target.jourdeb.selectedItem.toString()+"."+target.moisdeb.selectedItem.toString()+".2000")!!
        val fin:Date = DF.parse(target.jourdeb.selectedItem.toString()+"."+target.moisdeb.selectedItem.toString()+".2000")!!
        if(deb == null||fin==null){
            Toast.makeText(context,"Une date est  incorrecte !",Toast.LENGTH_SHORT).show()
            return
        }
        model.addPlanteArros(idp=idp,type=type,deb=deb,fin=fin,interval=target.edtextfreqj.text.toString().toInt())
    }
    fun checkDates():Boolean{
        val DF = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)
        for(target in [binding.arros1,binding.arros2,binding.arros3]){
        val  deb:Date? = DF.parse(binding.arros1.jourdeb.selectedItem.toString()+"."+target.moisdeb.selectedItem.toString()+".2000")
        val fin:Date? = DF.parse(target.jourdeb.selectedItem.toString()+"."+target.moisdeb.selectedItem.toString()+".2000")
        if(deb == null||fin==null){
            return false
        }
    }
        return true
    }
}