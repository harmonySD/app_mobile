package m1.pmob.veget_eau

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import m1.pmob.veget_eau.databinding.ChoixFreqBinding
import m1.pmob.veget_eau.databinding.FragmentModifierBinding
import java.text.SimpleDateFormat
import java.util.*

// 1 pull les arrosages  de la bd et les set up dans l'IG
// 2 réfléchir à une méthode de transfert des seuls changements par masque binaire !
// 3 transférer les seuls changements
// appliquer les changements

class ModifierFragment : Fragment(R.layout.fragment_modifier) {
    // =============FIELDS========================
    companion object {
        @JvmStatic
        fun newInstance()=ModifierFragment()
    }
    private lateinit var binding : FragmentModifierBinding  // binding pour gérer l'IG,
    //lateinit  car on a besoin d'une vue qui ne sera connu qu'à onViewCreated

    val model by lazy{// permet de récupérer le viewModel de l'APPLICATION grâce à de la réflexion Java
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    var b : Boolean =false // ce booléen sert à savoir si l'utilisateur a pris une photo ou  a chargé depuis son téléphone

    lateinit var imageView: ImageView // aperçu  de l'image de la plante que l'utilisateur va ajouter
    lateinit var  PlanteRepres: Eplante
    lateinit var  arrosPlanteRepres: Vector<Earrosage>
    var uri_path : Uri? = null // l'URI



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentModifierBinding.bind(view)
        imageView = binding.imageView
        var mChooseBtn= binding.chooseImageBtn
        var takeBn= binding.takeImageBtn
        var idPlanteRepres:Long= -1 // non on est sûr que tout s'est bien passé où on plante !


        val n=activity?.intent?.getLongExtra("plante",-1) // récupération id de la plante que l'utilisateur voulait modifier

        // récupération de toutes les données de la plante
        // on conditionne la suite du chargment de l'UI à la réussite de ces opérations
        model.loadPlanteByID(idPlanteRepres).observe(viewLifecycleOwner){
            PlanteRepres = it

            model.loadAllArrosByID(PlanteRepres.id).observe(viewLifecycleOwner){
                arrosPlanteRepres = it // attention ce it est le MutableLiveData<Vector<Earrosage>>
                setUI() // tout le chargement du reste de l'UI
                // est conditionné au chargement correct d'une plante et de ses éventuels arrosages
            }
        }


        //==================== ECOUTEURS POUR LA FREQUENCE DES ARROSAGES ==================

        //écouteurs des boutons pour activer les formulaires des arrosages
        binding.arros1.chckactiv.setOnClickListener {
            setActivationArrosElem(binding.arros1,binding.arros1.chckactiv.isChecked)
        }

        binding.arros2.chckactiv.setOnClickListener {
            setActivationArrosElem(binding.arros2,binding.arros2.chckactiv.isChecked)
        }

        binding.arros3.chckactiv.setOnClickListener {
            setActivationArrosElem(binding.arros3,binding.arros3.chckactiv.isChecked)
        }



        //==================== ECOUTEURS POUR LES BOUTONS DE CHOIX DE PHOTOS  ==================

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


       // ecouteur bouton supppression de plante
        binding.bSupp.setOnClickListener{
            //TODO  CONFIRMATION DE SUPPRESSION PUIS SUPPRESSION EFFECTIVE ET SORTIE DU FRAGMENT
            model.suppPlantesAndarros(PlanteRepres.id)
        }

        // écouteur du bouton modification
        binding.bModifierr.setOnClickListener {
            var ns = binding.edNomscient.text.toString().trim()
            var nc = binding.edNomverna.text.toString().trim()
            var uri = uri_path.toString()
            Log.d("uRI", "ici uri =$uri")
            Log.d("uRI", "$nc")



            if (nc == "" && ns == "") { // test qu'au moins un nom est renseigné
                afficherDialog("mettre au moins un nom :(")
                return@setOnClickListener // pour ne pas sortir de l'application !
            } else if (nc == "") {
                nc = "non communiqué"
            } else if (ns == "") {
                ns = "non communiqué"
            }
            if(!checkDates()){ // vérification des dates
                Toast.makeText(context,"Une date est  incorrecte !", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // pour ne pas sortir de l'application !

            }


            if(this::PlanteRepres.isInitialized) {

                Log.d("uRI","ici")
                if (PlanteRepres != null) {
                    if(PlanteRepres.nomscient!=ns){
                        if(PlanteRepres.nomverna!=nc){
                            if(PlanteRepres.uri!=uri){
                                modifPlanteAndArros(nc, ns, uri,idPlanteRepres)
                            }else{
                                modifPlanteAndArros(nc, ns, PlanteRepres.uri!!,idPlanteRepres)
                            }
                        }else{
                            if(PlanteRepres.uri!=uri){
                                modifPlanteAndArros(PlanteRepres.nomverna, ns, uri,idPlanteRepres)
                            }else{
                                modifPlanteAndArros(PlanteRepres.nomverna, ns, PlanteRepres.uri!!,idPlanteRepres)
                            }
                        }
                    }else {
                        if (PlanteRepres.nomverna != nc) {
                            if (PlanteRepres.uri != uri) {
                                modifPlanteAndArros(nc, PlanteRepres.nomscient, uri, idPlanteRepres)
                            } else {
                                Log.d("ici","ici dans else")
                                modifPlanteAndArros(nc, PlanteRepres.nomscient, PlanteRepres.uri!!, idPlanteRepres)
                            }
                        } else {
                            if (PlanteRepres.uri != uri) {
                                modifPlanteAndArros(PlanteRepres.nomverna, PlanteRepres.nomscient, uri, idPlanteRepres)
                            } else {
                                modifPlanteAndArros(PlanteRepres.nomverna, PlanteRepres.nomscient, PlanteRepres.uri!!, idPlanteRepres,)
                            }
                        }
                    }
                }
            }

        }

    }

    //=============================== AUX FUNCTIONS  ======================================

    fun setUI(){
        // L'UI sera modifié UNE FOIS QUE LA PLANTE AURA ETE EXTRAITE DE SA BD

        try { // tentative de chargement standard de la photo
            val bmp: Bitmap = BitmapFactory.decodeFile(PlanteRepres.uri)
            imageView.setImageBitmap(bmp)

        } catch (np: NullPointerException) {// si la photo est introuvable
            imageView.setImageDrawable(resources.getDrawable(R.drawable.tokenplant,null))
        }

        binding.edNomverna.setText(  if(PlanteRepres.nomverna =="non communiqu") "" else PlanteRepres.nomverna)
        binding.edNomscient.setText(  if(PlanteRepres.nomverna =="non communiqu") "" else PlanteRepres.nomscient)

        uri_path= PlanteRepres.uri?.toUri()
        //charger arrosage

        setActivationArrosElem(binding.arros1,false)
        setActivationArrosElem(binding.arros2,false)
        setActivationArrosElem(binding.arros3,false)
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

    private fun checkDates():Boolean{ // vérifie que les dates respectent bien un schéma correct avant ajout
        val DF = SimpleDateFormat("dd.MM.yyyy")
        for(target in arrayOf(binding.arros1,binding.arros2,binding.arros3)){
            val  deb: Date? = DF.parse((target.jourdeb.selectedItemPosition+1).toString()+"."+(target.moisdeb.selectedItemPosition+1).toString()+".2000")
            val fin: Date? = DF.parse((target.jourfin.selectedItemPosition+1).toString()+"."+(target.moisfin.selectedItemPosition+1).toString()+".2000")
            if(deb == null||fin==null){
                return false
            }
        }
        return true
    }

    fun setActivationArrosElem(target:ChoixFreqBinding, newStatus:Boolean){ //permet d'activer ou de désactiver une fréquence d'arrosage
        target.jourdeb.isEnabled = newStatus
        target.moisdeb.isEnabled = newStatus
        target.jourfin.isEnabled = newStatus
        target.moisfin.isEnabled = newStatus
        target.radbnormal.isEnabled = newStatus
        target.radbnutri.isEnabled = newStatus
        target.edtextfreqj.isEnabled = newStatus
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

    private fun modifPlanteAndArros( nc:String, ns:String, uri:String, pop:Long){ // modifie une plante et ses arrosages éventuels
        val tab = ArrayList<Earrosage>(0)
        for (e in arrayOf(  makeInexactArros(binding.arros1),
            makeInexactArros(binding.arros2),
            makeInexactArros(binding.arros3))){
            if(e != null){
                tab.add(e)
            }
        }
        Log.d("uRI", " dans appel $nc")
        model.modifPlanteandArros( //demande au viewmodel de faire ajouter dans la bd la modification de la plante et de ses arrosages
            n = nc, ns = ns, uri = uri, *(tab.toTypedArray()), pop=pop

        )
    }
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