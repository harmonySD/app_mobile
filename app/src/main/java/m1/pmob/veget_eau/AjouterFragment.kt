package m1.pmob.veget_eau

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import m1.pmob.veget_eau.databinding.ChoixFreqBinding
import m1.pmob.veget_eau.databinding.FragmentAjouterBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AjouterFragment : Fragment(R.layout.fragment_ajouter) {
    /*Fragment pour Ajouter une nouvelle plante à la base des plantes */


    companion object { // permet d'appeler le constructeur d'AjouterFragment de manière statique
        @JvmStatic
        fun newInstance() = AjouterFragment()
    }

    private lateinit var binding: FragmentAjouterBinding
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    // permet de récupérer le viewModel de l'APPLICATION grâce à de la réflexion Java

    lateinit var imageView: ImageView // aperçu  de l'image de la plante que l'utilisateur va ajouter
    var uri_path: Uri? = null // l'URI
    var b: Boolean =
        false //TODO virer ça // booléen pour savoir si l'utilisateur a pris une photo ou l'a sélectionné dans sa galerie
    // on créé / récupère le viewModel de l'application pour faire des travaux en arrière plan


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // pour survivre aux rotations d'écran

        // on lie l'interface graphique au code
        binding = FragmentAjouterBinding.bind(view)
        imageView = binding.imageView
        var mChooseBtn = binding.chooseImageBtn
        var takeBn = binding.takeImageBtn

        mChooseBtn.setOnClickListener { // écouteur pour le bouton de demande de choix de photo dans la galerie
            b = true
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getResult.launch(gallery)
        }

        takeBn.setOnClickListener { // écouteur pour le bouton de demande de capture de photo pour la plante
            b = false
            // on a besoin d'avoir les droits en lecture et en écriture sur le storage externe
            // on regarde si on a les droits en lecture
            var allowedexternR = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            // on regarde si on a les droits en écriture
            var allowedexternW = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            if (PackageManager.PERMISSION_GRANTED != allowedexternR
                || PackageManager.PERMISSION_GRANTED != allowedexternW
            ){ // si on est ici c'est qu'on n'a pas tous les droits requis
                    permissionAsker.launch(
                        arrayOf<String>(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }else{
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    getResult.launch(cameraIntent)
                }
            }





        binding.bAjouter.setOnClickListener()
        { // écouteur du bouton d'ajout de plante
            // on enlève un éventuel excès d'espace dans le nom des plantes
            var nc = binding.edNomverna.text.toString().trim()
            var ns = binding.edNomscient.text.toString().trim()

            var uri = uri_path.toString()


            if (nc == "" && ns == "") { // test qu'au moins un nom est renseigné
                afficherDialog("mettre au moins un nom :(")
                return@setOnClickListener // pour ne pas sortir de l'application !
            } else if (nc == "") {
                nc = "non communiqué"
            } else if (ns == "") {
                ns = "non communiqué"
            }

            if (!checkArros()) { // vérification des dates
                afficherDialog("Un arrosage  est  incorrect !")
                return@setOnClickListener // pour ne pas sortir de l'application !
            }



            addPlanteAndArros(nc, ns, uri)

            //vidage des champs pour prochaine entrée
            binding.edNomscient.text.clear()
            binding.edNomverna.text.clear()
            //binding.edUri.text.clear()
            binding.imageView.setImageBitmap(null)
            uri_path = null
            clearAllArros() // remise à zéro de tous les arrosages pour nouvelle entrée
        }

        //==================== ECOUTEURS POUR LA FREQUENCE DES ARROSAGES ==================

        //écouteurs des boutons pour activer les formulaires des arrosages
        binding.arros1.chckactiv.setOnClickListener()
        {
            activateArrosElem(binding.arros1, binding.arros1.chckactiv.isChecked)
        }

        binding.arros2.chckactiv.setOnClickListener()
        {
            activateArrosElem(binding.arros2, binding.arros2.chckactiv.isChecked)
        }

        binding.arros3.chckactiv.setOnClickListener()
        {
            activateArrosElem(binding.arros3, binding.arros3.chckactiv.isChecked)
        }

        // désactivation de tous les formulaires d'arrosage au lancement du fragment
        activateArrosElem(binding.arros1, false)
        activateArrosElem(binding.arros2, false)
        activateArrosElem(binding.arros3, false)
    }


    fun afficherDialog(s: String) {
        context?.let {
            AlertDialog.Builder(it)
                .setMessage(s)
                .setPositiveButton("OK") { d, _ -> d.dismiss() }
                .show()
        }
        return
    }

    fun activateArrosElem(
        target: ChoixFreqBinding,
        newStatus: Boolean
    ): Unit { //permet d'activer ou de désactiver une fréquence d'arrosage
        target.jourdeb.isEnabled = newStatus
        target.moisdeb.isEnabled = newStatus
        target.jourfin.isEnabled = newStatus
        target.moisfin.isEnabled = newStatus
        target.radbnormal.isEnabled = newStatus
        target.radbnutri.isEnabled = newStatus
        target.edtextfreqj.isEnabled = newStatus
    }

    private fun addPlanteAndArros(
        nc: String,
        ns: String,
        uri: String
    ) {//ajoute la plante et les arrosages à la BD
        val tab = ArrayList<Earrosage>(0)
        for (e in arrayOf(
            makeInexactArros(binding.arros1),
            makeInexactArros(binding.arros2),
            makeInexactArros(binding.arros3)
        )) {
            if (e != null) {
                tab.add(e)
            }
        }
        model.addPlantesandArros( //demande au viewmodel de faire ajouter dans la bd la plante et ses arrosage
            n = nc, ns = ns, uri = uri, *(tab.toTypedArray())
        )
    }

    private fun makeInexactArros(target: ChoixFreqBinding): Earrosage? { // créé une entite d'arrosage avec un idplante inconnu pour servir temporairement.
        //TODO cette fonction sera très probablement réutilisable/déplaçable autre part!
        if (!target.chckactiv.isChecked) {
            return null
        } // si la fréquence n'est pas "activée", on retourne !
        val type = if (target.radbnormal.isChecked) Typearros.STANDARD else Typearros.NUTRITIF
        val DF = SimpleDateFormat("dd.MM.yyyy")
        val deb: Date =
            DF.parse((target.jourdeb.selectedItemPosition + 1).toString() + "." + (target.moisdeb.selectedItemPosition + 1).toString() + ".2000")!!
        val fin: Date =
            DF.parse((target.jourfin.selectedItemPosition + 1).toString() + "." + (target.moisfin.selectedItemPosition + 1).toString() + ".2000")!!

        return Earrosage(
            idp = 0,
            type = type,
            deb = deb,
            fin = fin,
            interval = target.edtextfreqj.text.toString().toInt()
        )
    }

    private fun checkArros(): Boolean { // vérifie que les dates respectent bien un schéma correct avant ajout
        // et que la fréquence est valide
        val DF = SimpleDateFormat("dd.MM.yyyy")
        for (target in arrayOf(binding.arros1, binding.arros2, binding.arros3)) {
            if (!target.chckactiv.isChecked) {
                continue
            }
            val deb: Date? =
                DF.parse((target.jourdeb.selectedItemPosition + 1).toString() + "." + (target.moisdeb.selectedItemPosition + 1).toString() + ".2000")
            val fin: Date? =
                DF.parse((target.jourfin.selectedItemPosition + 1).toString() + "." + (target.moisfin.selectedItemPosition + 1).toString() + ".2000")
            if (deb == null || fin == null) {
                return false
            }

            try {// vérification que le champ de fréquence est bien un nombre
                target.edtextfreqj.text.toString().toInt()
            } catch (e: Exception) {
                return false
            }
        }
        return true
    }

    private fun clearAllArros() { // vide les champs d'arros
        for (target in arrayOf(binding.arros1, binding.arros2, binding.arros3)) {
            target.edtextfreqj.text.clear()
            target.jourdeb.setSelection(0)
            target.moisdeb.setSelection(0)
            target.jourfin.setSelection(0)
            target.moisfin.setSelection(0)
            target.radbnormal.isChecked = false
            target.radbnutri.isChecked = false
            target.chckactiv.isChecked = false
            activateArrosElem(target, false)
        }

    }

    // sauvegarder l'image de la camera dans la gallerie pour l'enregistrer ensuite
//inspiré de https://android--code.blogspot.com/2018/04/android-kotlin-save-image-to-gallery.html
    private fun saveImage(bitmap: Bitmap, title: String): Uri {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            title,
            "Image of $title"
        )
        return Uri.parse(savedImageURL)
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
                if (b) {
                    //marche pour prendre depuis gallery

                    imageView.setImageURI(it.data?.data)
                } else if (!b) {
                    //marche pour appareil photo
                    imageView.setImageBitmap(it.data?.extras?.get("data") as Bitmap)
                    val test = it.data?.extras?.get("data") as Bitmap
                    uri_path = saveImage(test, "plante")
                }
            }
        }
    //ce chammp sotcke un lanceur d'activité pour demander les droits en écriture
    // sur périphérique de stockage externe.
    // il sert pour pouvoir prendre une photo puis la stocker directement
    private val permissionAsker = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[Manifest.permission.READ_EXTERNAL_STORAGE]!! && it[Manifest.permission.WRITE_EXTERNAL_STORAGE]!!) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getResult.launch(cameraIntent)
        }}
}