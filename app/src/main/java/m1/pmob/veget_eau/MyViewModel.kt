package m1.pmob.veget_eau

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class MyViewModel(application: Application): AndroidViewModel(application) {
    val dao = PlantsDatabase.getDatabase(application).myDao()
    //pas besoin de recupere elt de la liste
    val plantes = dao.getAllPlants()
    //pour recherche plantes avec prefixes
    var certainesPlantes = MutableLiveData<List<Eplante>>()
    var listeArros = MutableLiveData<List<Earrosage>>()
    val appcontext= application.applicationContext



    fun addPlantes(n: String, ns: String, uri: String?){
        Thread{
            dao.ajoutPlante(Eplante(nomverna = n.trim(),nomscient = ns.trim(), uri = uri?.trim()))
        }.start()
    }
    fun addArros(idp:Long, type:Typearros,interval:Int, deb: Date, fin:Date){
        Thread{
            dao.ajoutArros(Earrosage(idp=idp,type=type,interval = interval,deb=deb,fin=fin))
        }.start()
    }
    fun suppPlantesAndarros(id:Long) {
       //TODO penser à supprimer l'image  aussi !
        Thread {
            dao.supprPlante(id)
            dao.supprAllArros(id)
        }.start()
    }

    fun loadPlanteByID(idsrch:Long):MutableLiveData<Eplante>{
        val plantholder : MutableLiveData<Eplante> = MutableLiveData<Eplante>()
        Thread{

            plantholder.postValue(dao.loadPlanteByID(idsrch))
        }.start()
        return  plantholder
    }

    fun loadAllArrosByID(idp:Long):MutableLiveData<Vector<Earrosage>>{
        val arrosHolder : MutableLiveData<Vector<Earrosage>> = MutableLiveData<Vector<Earrosage>>()
        Thread{

            arrosHolder.postValue(Vector<Earrosage>(dao.getPlantArros(idp)))
        }.start()
        return  arrosHolder
    }

    fun getPlantesPrefix(p:String){
        Thread {
            Log.d("uRI", "vmod${dao.loadPartialName(p)}")
            certainesPlantes.postValue(dao.loadPartialName(p))
        }.start()
    }


    fun modifPlanteandArros(changep: Eplante,vararg lstarros:Earrosage){
        Thread{
            var plantToWrite = changep
            // on regarde si l'image dans l'uri a ete modifiée ou pas
            // probleme que fait on de l'ancienne image ?????
            if(!( changep.uri !=null && appcontext.cacheDir.resolve((changep.uri)).exists())){
                //si on entre ici c'est que l'image à stocker a été modifiée ou était null
                try {
                    val toread = appcontext.contentResolver.openInputStream(Uri.parse(changep.uri))!!
                    val filetowrite = File(appcontext.cacheDir, changep.nomverna .trim() + "" + changep.nomscient.trim() +  changep.id)
                    val newpth = filetowrite.toString()
                    val towrite = FileOutputStream(filetowrite)
                    toread.copyTo(towrite)
                    toread.close()
                    towrite.close()
                    // on applique le changement d'URI
                    plantToWrite = Eplante(changep.id, changep.nomscient, changep.nomverna,newpth)
                    //chemin vers le nouveau fichier contenant l'image
                }catch(fne: FileNotFoundException){ // si le fichier n'existe pas, on ajoute l'image standard de plante
                    plantToWrite = Eplante(
                        changep.id,
                        changep.nomscient,
                        changep.nomverna,
                        null
                    )
                }
            }

            val ret : Int = dao.modifPlante(plantToWrite)
            dao.supprAllArros(plantToWrite.id)
            for (arros in lstarros){
                dao.ajoutArros(arros)
            }
        }.start()
    }


    fun addPlantesandArros(n:String,ns:String,uri:String?,vararg lstfakearros:Earrosage){
        Thread{

            val ret : List<Long> = dao.ajoutPlante(Eplante(nomverna = n.trim(),nomscient = ns.trim(), uri = uri?.trim()))
            for (fakearros in lstfakearros){
                Log.d("MYVIEWMODEL:ajoutplante", "${fakearros.id} ${fakearros.type} ${fakearros.interval} ${fakearros.deb} ${fakearros.fin} ")
                dao.ajoutArros(Earrosage(idp=ret[0],type=fakearros.type,interval = fakearros.interval,deb=fakearros.deb,fin=fakearros.fin))

            }
            Log.d("URI viewmodel",uri!!)

            if(uri!=null){
                //try{} // FAIRE UN TRY POUR ATTRAPPER LES URI INCORRECTS

                    try {
                        val toread = appcontext.contentResolver.openInputStream(Uri.parse(uri))!!
                        val filetowrite =
                            File(appcontext.cacheDir, n.trim() + "" + ns.trim() + "" + ret[0])
                        val towrite = FileOutputStream(filetowrite)
                        toread.copyTo(towrite)
                        toread.close()
                        towrite.close()

                        dao.modifPlante(
                            Eplante(
                                ret[0], // id de  la plante qu'on souhaite modifier
                                n.trim(), // nom normal à ne pas modifier
                                ns.trim(), // nom scientifique à ne pas modifier
                                appcontext.cacheDir.resolve(n.trim() + "" + ns.trim() + "" + ret[0]).toString()))
                                //chemin vers le nouveau fichier contenant l'image
                    }catch(fne: FileNotFoundException){ // si le fichier n'existe pas, on ajoute l'image standard de plante
                        dao.modifPlante(
                            Eplante(
                                ret[0], // id de  la plante qu'on souhaite modifier
                                n.trim(), // nom normal à ne pas modifier
                                ns.trim(), // nom scientifique à ne pas modifier
                               null))

                    }
            }
        }.start()
    }


}