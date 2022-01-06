package m1.pmob.veget_eau

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
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
        fun newInstance() = ModifierFragment()
    }

    private lateinit var binding: FragmentModifierBinding  // binding pour gérer l'IG,
    //lateinit  car on a besoin d'une vue qui ne sera connu qu'à onViewCreated !

    val model by lazy {// permet de récupérer le viewModel de l'APPLICATION grâce à de la réflexion Java
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    var b: Boolean =
        false // ce booléen sert à savoir si l'utilisateur a pris une photo ou  a chargé depuis son téléphone

    lateinit var imageView: ImageView // aperçu  de l'image de la plante que l'utilisateur va ajouter
    lateinit var PlanteRepres: Eplante
    lateinit var arrosPlanteRepres: Vector<Earrosage>
    var uri_path: Uri? = null // l'URI


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // pour survivre aux rotations d'écran
        binding = FragmentModifierBinding.bind(view)
        imageView = binding.imageView
        var mChooseBtn = binding.chooseImageBtn
        var takeBn = binding.takeImageBtn


        // récupération id de la plante que l'utilisateur voulait modifier
        val idPlanteRepres = activity?.intent?.getLongExtra("plante", -1)!!

        // récupération de toutes les données de la plante
        // on conditionne la suite du chargment de l'UI à la réussite de ces opérations
        model.loadPlanteByID(idPlanteRepres).observe(viewLifecycleOwner) {
            PlanteRepres = it

            model.loadAllArrosByID(PlanteRepres.id).observe(viewLifecycleOwner) {
                arrosPlanteRepres = it // attention ce it est le MutableLiveData<Vector<Earrosage>>
                setUI() // tout le chargement du reste de l'UI
                // est conditionné au chargement correct d'une plante et de ses éventuels arrosages
            }
        }


        //==================== ECOUTEURS POUR LA FREQUENCE DES ARROSAGES ==================

        //écouteurs des boutons pour activer les formulaires des arrosages
        binding.arros1.chckactiv.setOnClickListener {
            setActivationArrosElem(binding.arros1, binding.arros1.chckactiv.isChecked)
        }

        binding.arros2.chckactiv.setOnClickListener {
            setActivationArrosElem(binding.arros2, binding.arros2.chckactiv.isChecked)
        }

        binding.arros3.chckactiv.setOnClickListener {
            setActivationArrosElem(binding.arros3, binding.arros3.chckactiv.isChecked)
        }

        //==================== ECOUTEURS POUR LES BOUTONS DE CHOIX DE PHOTOS  ==================

        mChooseBtn.setOnClickListener { // écouteur pour le bouton de demande de choix de photo dans la galerie
            b = true
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getResult.launch(gallery)
        }

        takeBn.setOnClickListener { // écouteur pour le bouton de demande de capture de photo pour la plante
            b = false
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

        // ecouteur bouton supppression de plante
        binding.bSupp.setOnClickListener {
            context?.let { it1 ->
                AlertDialog.Builder(it1)
                    .setMessage("supprimer vraiment cette plante?") .setCancelable(false)
                    .setPositiveButton("OK") { d, _ ->
                        model.suppPlantesAndarros(PlanteRepres.id)
                        d.dismiss()
                    }
                    .setNegativeButton("NON") { d, _ -> d.dismiss() } .show()
            }
        }

        // écouteur du bouton modification
        binding.bModifierr.setOnClickListener {
            var ns = binding.edNomscient.text.toString().trim()
            var nc = binding.edNomverna.text.toString().trim()



            if (nc == "" && ns == "") { // test qu'au moins un nom est renseigné
                afficherDialog("Une plante doit avoir au moins un nom !")
                return@setOnClickListener // pour ne pas sortir de l'application !
            }

            nc = if (nc == "") "non communiqué" else nc
            ns = if (ns == "") "non communiqué" else ns

            if (!checkArros()) { // vérification des dates
                Toast.makeText(context, "Une date est  incorrecte !", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // pour ne pas sortir de l'application !
            }

            val nvEarros: Vector<Earrosage> = Vector<Earrosage>()
            for (possib in arrayOf(binding.arros1, binding.arros2, binding.arros3)) {
                if (possib.chckactiv.isChecked) {
                    nvEarros.addElement(makeInexactArros(possib))
                }
            }

            modifPlanteAndArros(ns, nc, nvEarros)

        }
    }

    //=============================== AUX FUNCTIONS  ======================================


    fun setUI() {
        // L'UI sera modifié UNE FOIS QUE LA PLANTE AURA ETE EXTRAITE DE SA BD

        try { // tentative de chargement standard de la photo
            val bmp: Bitmap = BitmapFactory.decodeFile(PlanteRepres.uri)
            imageView.setImageBitmap(bmp)

        } catch (np: NullPointerException) {// si la photo est introuvable
            imageView.setImageDrawable(resources.getDrawable(R.drawable.tokenplant, null))
        }

        binding.edNomverna.setText(if (PlanteRepres.nomverna == "non communiqu") "" else PlanteRepres.nomverna)
        binding.edNomscient.setText(if (PlanteRepres.nomscient == "non communiqu") "" else PlanteRepres.nomscient)

        uri_path = PlanteRepres.uri?.toUri()

        if (arrosPlanteRepres.size > 0) {
            setArrosElem(binding.arros1, arrosPlanteRepres[0])
        } else setArrosElem(binding.arros1, null)

        if (arrosPlanteRepres.size > 1) {
            setArrosElem(binding.arros2, arrosPlanteRepres[1])
        } else setArrosElem(binding.arros2, null)

        if (arrosPlanteRepres.size > 2) {
            setArrosElem(binding.arros3, arrosPlanteRepres[2])
        } else setArrosElem(binding.arros3, null)


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

    fun setActivationArrosElem(
        target: ChoixFreqBinding,
        newStatus: Boolean
    ) { //permet d'activer ou de désactiver une fréquence d'arrosage
        target.jourdeb.isEnabled = newStatus
        target.moisdeb.isEnabled = newStatus
        target.jourfin.isEnabled = newStatus
        target.moisfin.isEnabled = newStatus
        target.radbnormal.isEnabled = newStatus
        target.radbnutri.isEnabled = newStatus
        target.edtextfreqj.isEnabled = newStatus
    }

    fun setArrosElem(
        target: ChoixFreqBinding,
        arrs: Earrosage?
    ) { // cette fonction sert à charger le contenu d'un arrosage dans son UI
        if (arrs == null) {// si l'arrosage est null, on éteint l'UI et on retourne
            setActivationArrosElem(target, true)
            return
        }
        // il y a un arrosage à charger
        target.chckactiv.isChecked = true
        setActivationArrosElem(target, true)
        val calend = Calendar.getInstance()
        calend.timeInMillis = arrs.deb.time
        target.jourdeb.setSelection(calend.get(Calendar.DAY_OF_MONTH) - 1)
        target.moisdeb.setSelection(calend.get(Calendar.MONTH))

        calend.timeInMillis = arrs.fin.time
        target.jourfin.setSelection(calend.get(Calendar.DAY_OF_MONTH) - 1)
        target.moisfin.setSelection(calend.get(Calendar.MONTH) - 1)

        if (arrs.type == Typearros.STANDARD) {
            target.radbnormal.isChecked = true
            target.radbnutri.isChecked = false
        } else {
            target.radbnormal.isChecked = false
            target.radbnutri.isChecked = true
        }

        target.edtextfreqj.text.clear()
        target.edtextfreqj.text.insert(0, arrs.interval.toString())

    }

    private fun makeInexactArros(target: ChoixFreqBinding): Earrosage? { // créé une entite d'arrosage avec un idplante inconnu pour servir temporairement.

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
            idp = PlanteRepres.id,
            type = type,
            deb = deb,
            fin = fin,
            interval = target.edtextfreqj.text.toString().toInt()
        )
    }

    private fun modifPlanteAndArros(
        scientName: String,
        vernaName: String,
        ArrosVect: Vector<Earrosage>
    ) { // modifie une plante et ses arrosages éventuels
        //demande au viewmodel de faire ajouter dans la bd la modification de la plante et de ses arrosages
        model.modifPlanteandArros(
            Eplante(PlanteRepres.id, vernaName, scientName, uri_path.toString()),
            *(ArrosVect.toTypedArray())
        )
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
        }
    }
}

