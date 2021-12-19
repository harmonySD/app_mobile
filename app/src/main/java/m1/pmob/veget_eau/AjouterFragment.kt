package m1.pmob.veget_eau

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    /*Fragment pour Ajouter une nouvelle plante à la base des plantes */


    companion object { // permet d'appeler le constructeur d'AjouterFragment de manière statique
        @JvmStatic
        fun newInstance()=AjouterFragment()
    }

    private lateinit var binding : FragmentAjouterBinding
    val model by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)}
        // permet de récupérer le viewModel de l'APPLICATION grâce à de la réflexion Java

    lateinit var imageView: ImageView // aperçu  de l'image de la plante que l'utilisateur va ajouter
    var uri_path : Uri? = null // l'URI
    var b : Boolean =false //TODO virer ça // booléen pour savoir si l'utilisateur a pris une photo ou l'a sélectionné dans sa galerie
    //ajouter ca dans bd

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState) // pour survivre aux rotations d'écran

        // on lie l'interface graphique au code
        binding = FragmentAjouterBinding.bind(view)
        imageView = binding.imageView
        var mChooseBtn= binding.chooseImageBtn
        var takeBn= binding.takeImageBtn


        mChooseBtn.setOnClickListener{ // écouteur pour le bouton de demande de choix de photo dans la galerie
            b=true
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getResult.launch(gallery)
        }

        takeBn.setOnClickListener{ // écouteur pour le bouton de demande de capture de photo pour la plante
            b=false
            val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getResult.launch(cameraIntent)
        }



        binding.bAjouter.setOnClickListener { // écouteur du bouton d'ajout de plante
            // on enlève un éventuel excès d'espace dans le nom des plantes
            var nc = binding.edNomverna.text.toString().trim()
            var ns = binding.edNomscient.text.toString().trim()

            var uri = uri_path.toString()
            Log.d("uRI", "ici $uri")



            if (nc == "" && ns == "") { // test qu'au moins un nom est renseigné
                afficherDialog("mettre au moins un nom :(")
                return@setOnClickListener // pour ne pas sortir de l'application !
            } else if (nc == "") {
                nc = "non communiqué"
            } else if (ns == "") {
                ns = "non communiqué"
            }

            if(!checkDates()){ // vérification des dates
                Toast.makeText(context,"Une date est  incorrecte !",Toast.LENGTH_SHORT).show()
                return@setOnClickListener // pour ne pas sortir de l'application !

            }

            addPlanteAndArros(nc,ns,uri)

            //vidage des champs pour prochaine entrée
            binding.edNomscient.text.clear()
            binding.edNomverna.text.clear()
            binding.edUri.text.clear()
            binding.imageView.setImageBitmap(null)
            uri_path=null
            clearAllArros() // remise à zéro de tous les arrosages pour nouvelle entrée

        }

        //==================== ECOUTEURS POUR LA FREQUENCE DES ARROSAGES ==================

        //écouteurs des boutons pour activer les formulaires des arrosages
        binding.arros1.chckactiv.setOnClickListener {
            activateArrosElem(binding.arros1,binding.arros1.chckactiv.isChecked)
        }

        binding.arros2.chckactiv.setOnClickListener {
            activateArrosElem(binding.arros2,binding.arros2.chckactiv.isChecked)
            }

        binding.arros3.chckactiv.setOnClickListener {
            activateArrosElem(binding.arros3,binding.arros3.chckactiv.isChecked)
        }

        // désactivation de tous les formulaires d'arrosage au lancement du fragment
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
        model.addPlantesandArros( //demande au viewmodel de faire ajouter dans la bd la plante et ses arrosage
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

    // ce champ stocke un lanceur d'activité attendant un résultat
    // Il sert pour lancer l'une des deux activités de choix d'image pour la plante
    // (par galerie ou par appareil photo)
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                uri_path = it.data?.data
                Log.d("URI LeData","${it.data?.data}")
                Log.d("URI LeDataPath","${it.data?.data!!.path}")
                    if(b){
                        //marche pour prendre depuis gallery
                        imageView.setImageURI(it.data?.data)
                    }else if(!b){
                        //marche pour appareil photo
                        imageView.setImageBitmap(it.data?.extras?.get("data") as Bitmap)
                    }
            }
        }
}
