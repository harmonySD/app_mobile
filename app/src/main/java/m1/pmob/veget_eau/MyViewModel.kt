package m1.pmob.veget_eau

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
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
    val appcontext= application.applicationContext
    //plantebn pb cest tjs a 0 :(
   //var plantebn = Eplante(0,"","","")


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


    fun getPlantesPrefix(p:String){
        Thread {
            Log.d("uRI", "vmod${dao.loadPartialName(p)}")
            certainesPlantes.postValue(dao.loadPartialName(p))
        }.start()
    }


    fun addPlantesandArros(n:String,ns:String,uri:String?,vararg lstfakearros:Earrosage){
        Thread{

            val ret : List<Long> = dao.ajoutPlante(Eplante(nomverna = n.trim(),nomscient = ns.trim(), uri = uri?.trim()))
            for (fakearros in lstfakearros){
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
   // fun getPlanteByName(n:String?):Eplante{
     //   lateinit var plantebn:Eplante
       // Thread{
         //   Log.d("uRI", "vmod${dao.loadExactName(n)}")
           //  plantebn= dao.loadExactName(n);
     //   }.start()
       // return plantebn
   // }


}