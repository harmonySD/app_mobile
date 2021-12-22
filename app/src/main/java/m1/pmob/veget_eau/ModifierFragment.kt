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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import m1.pmob.veget_eau.databinding.ChoixFreqBinding
import m1.pmob.veget_eau.databinding.FragmentAjouterBinding
import m1.pmob.veget_eau.databinding.FragmentModifierBinding
import java.text.SimpleDateFormat
import java.util.*


class ModifierFragment : Fragment(R.layout.fragment_modifier) {

    companion object {
        @JvmStatic
        fun newInstance()=ModifierFragment()
    }
    private lateinit var binding : FragmentModifierBinding
    val model by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    // permet de récupérer le viewModel de l'APPLICATION grâce à de la réflexion Java
    var b : Boolean =false
    lateinit var imageView: ImageView // aperçu  de l'image de la plante que l'utilisateur va ajouter
    var uri_path : Uri? = null // l'URI

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentModifierBinding.bind(view)
        imageView = binding.imageView
        var mChooseBtn= binding.chooseImageBtn
        var takeBn= binding.takeImageBtn
        var ns=binding.edNomscient
        var nv=binding.edNomverna
        var pop:Long= -1

        // remplir avec info de la plante

        val n=activity?.intent?.getLongExtra("plante",-1) // on récupère l id de la plante que l'utilisateur voulait charger
        var pl= n?.let { getPlante(it.toLong()) }

        if (pl != null) { // si on a bien trouver la plante que l'utilisateur voulait charger
            try { // tentative de chargement standard de la photo
                val bmp: Bitmap = BitmapFactory.decodeFile(pl.uri)
                imageView.setImageBitmap(bmp)

            } catch (np: NullPointerException) {// si la photo est introuvable
                imageView.setImageDrawable(resources.getDrawable(R.drawable.tokenplant))
            }
            if(pl.nomverna=="non communiqué"){
                ns.setText(pl.nomscient)

            }else if(pl.nomscient=="non communiqué"){
                nv.setText(pl.nomverna)
            }else{
                nv.setText(pl.nomverna)
                ns.setText(pl.nomscient)
            }
            pop=pl.id
            Log.d("uRI", "la uri =${pl.uri}")
            Log.d("uRI", "la uri =${pl.uri?.toUri()}")
            uri_path= pl.uri?.toUri()

        }
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



        binding.bModifierr.setOnClickListener { // écouteur du bouton modification
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
            Log.d("uRI","ici la ")

            if(pop>-1) {
                Log.d("uRI","ici")
                if (pl != null) {
                    if(pl.nomscient!=ns){
                        if(pl.nomverna!=nc){
                            if(pl.uri!=uri){
                                modifPlanteAndArros(nc, ns, uri,pop)
                            }else{
                                modifPlanteAndArros(nc, ns, pl.uri!!,pop)
                            }
                        }else{
                            if(pl.uri!=uri){
                                modifPlanteAndArros(pl.nomverna, ns, uri,pop)
                            }else{
                                modifPlanteAndArros(pl.nomverna, ns, pl.uri!!,pop)
                            }
                        }
                    }else {
                        if (pl.nomverna != nc) {
                            if (pl.uri != uri) {
                                modifPlanteAndArros(nc, pl.nomscient, uri, pop)
                            } else {
                                Log.d("ici","ici dans else")
                                modifPlanteAndArros(nc, pl.nomscient, pl.uri!!, pop)
                            }
                        } else {
                            if (pl.uri != uri) {
                                modifPlanteAndArros(pl.nomverna, pl.nomscient, uri, pop)
                            } else {
                                modifPlanteAndArros(pl.nomverna, pl.nomscient, pl.uri!!, pop,)
                            }
                        }
                    }
                }
            }

        }

    }

    //=============================== FONCTIONS AUX ======================================
    fun getPlante(n: Long):Eplante{
        var p= Eplante(0, "", "", "")
        var thread = Thread{
            p = (model.dao.loadExactName(n))
        }
        thread.start()
        thread.join()
        return p
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
    private fun makeInexactArros(target: ChoixFreqBinding):Earrosage?{ // créé une entite d'arrosage avec un idplante inconnu pour servir temporairement.
        //TODO cette fonction sera très probablement réutilisable/déplaçable autre part!
        if(!target.chckactiv.isChecked){return null} // si la fréquence n'est pas "activée", on retourne !
        val type =  if(target.radbnutri.isSelected ) Typearros.STANDARD else Typearros.NUTRITIF
        val DF = SimpleDateFormat("dd.MM.yyyy")
        val deb:Date = DF.parse((target.jourdeb.selectedItemPosition+1).toString()+"."+(target.moisdeb.selectedItemPosition+1).toString()+".2000")!!
        val fin:Date = DF.parse((target.jourfin.selectedItemPosition+1).toString()+"."+(target.moisfin.selectedItemPosition+1).toString()+".2000")!!

        return Earrosage(idp=0,type=type,deb=deb,fin=fin,interval=target.edtextfreqj.text.toString().toInt())
    }

    private fun modifPlanteAndArros( nc:String, ns:String, uri:String, pop:Long){//ajoute la plante et les arrosages à la BD
        val tab = ArrayList<Earrosage>(0)
        for (e in arrayOf(  makeInexactArros(binding.arros1),
            makeInexactArros(binding.arros2),
            makeInexactArros(binding.arros3))){
            if(e != null){
                tab.add(e)
            }
        }
        Log.d("uRI", " dans appel $nc")
        model.modifPlanteandArros( //demande au viewmodel de faire ajouter dans la bd la plante et ses arrosage
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