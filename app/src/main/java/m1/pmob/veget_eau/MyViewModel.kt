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
    fun suppPlantesAndarros(id:Long,n:String,ns:String,uri:String?,vararg lstfakearros:Earrosage) {
       // Log.d("tchooo", "bah ici hors thread  dans view model $id")
        Thread {
            Log.d("tchooo", "bah ici  dans view model $id")
            for (fakearros in lstfakearros) {
                dao.supprArros(
                    Earrosage(
                        idp = id,
                        type = fakearros.type,
                        interval = fakearros.interval,
                        deb = fakearros.deb,
                        fin = fakearros.fin
                    )
                )

            }

            val int: Int =
                dao.supprPlante(
                    Eplante(
                        id = id,
                        nomverna = n.trim(),
                        nomscient = ns.trim(),
                        uri = uri?.trim()
                    )
                )
        }.start()
    }


    fun getPlantesPrefix(p:String){
        Thread {
            Log.d("uRI", "vmod${dao.loadPartialName(p)}")
            certainesPlantes.postValue(dao.loadPartialName(p))
        }.start()
    }

    fun getArrosForP(p:Long){
        Thread {
            listeArros.postValue(dao.getPlantArros(p))
        }.start()

    }

    fun modifPlanteandArros(n:String,ns:String,uri:String?,vararg lstfakearros:Earrosage,pop:Long){
        Thread{
            Log.d("MYVIEWMODEL:modifplante", " n=$n ns=$ns uri=$uri pop = $pop")
            val ret : Int = dao.modifPlante(Eplante(id = pop,nomverna = n.trim(),nomscient = ns.trim(), uri = uri?.trim()))
            for (fakearros in lstfakearros){
                Log.d("MYVIEWMODEL:modifplante", "${fakearros.id} ${fakearros.type} ${fakearros.interval} ${fakearros.deb} ${fakearros.fin} pop = $pop")
                dao.modifArros(Earrosage(id=fakearros.id,idp=pop,type=fakearros.type,interval = fakearros.interval,deb=fakearros.deb,fin=fakearros.fin))
            }
            Log.d("URI viewmodel",uri!!)
            Log.d("uRI", " dans appel $n")

            if(uri!=null){
                //try{} // FAIRE UN TRY POUR ATTRAPPER LES URI INCORRECTS
                try {
                    val toread = appcontext.contentResolver.openInputStream(Uri.parse(uri))!!
                    val filetowrite =
                        File(appcontext.cacheDir, n.trim() + "" + ns.trim() + "" + ret.toLong())
                    val towrite = FileOutputStream(filetowrite)
                    toread.copyTo(towrite)
                    toread.close()
                    towrite.close()

                    dao.modifPlante(
                        Eplante(
                            pop.toLong(), // id de  la plante qu'on souhaite modifier
                            n.trim(), // nom normal à ne pas modifier
                            ns.trim(), // nom scientifique à ne pas modifier
                            appcontext.cacheDir.resolve(n.trim() + "" + ns.trim() + "" + ret.toLong()).toString()))
                    //chemin vers le nouveau fichier contenant l'image
                }catch(fne: FileNotFoundException){ // si le fichier n'existe pas, on ajoute l'image standard de plante
                    dao.modifPlante(
                        Eplante(
                            pop , // id de  la plante qu'on souhaite modifier
                            n.trim(), // nom normal à ne pas modifier
                            ns.trim(), // nom scientifique à ne pas modifier
                            uri))
                }
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