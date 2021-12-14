package m1.pmob.veget_eau

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.collections.ArrayList

class MyViewModel(application: Application): AndroidViewModel(application) {
    val dao = PlantsDatabase.getDatabase(application).myDao()
    //pas besoin de recuppere elt de la liste
    val plantes = dao.getAllPlants()
    //pour recherche plabtes avec prefixes
    var certainesPlantes = MutableLiveData<List<Eplante>>()
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