package m1.pmob.veget_eau

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData

class MyViewModel(application: Application): AndroidViewModel(application) {
    val dao = PlantsDatabase.getDatabase(application).myDao()
    //pas besoin de recuppere elt de la liste
    val plantes = dao.getAllPlants()
    //pour recherche plabtes avec prefixes
    var certainesPlantes = MutableLiveData<List<Eplante>>()

    fun addPlantes(n: String, ns: String, uri: String?){
        Thread{
            dao.ajoutPlante(Eplante(nomverna = n.trim(),nomscient = ns.trim(), uri = uri?.trim()))
        }.start()
    }

    fun getPlantesPrefix(p:String){
        //Log.d("getPlante", "p=$p")
        Thread {
            Log.d("getPlante", "${dao.loadPartialName(p)}")
            certainesPlantes.postValue(dao.loadPartialName(p))
        }.start()

    }

}