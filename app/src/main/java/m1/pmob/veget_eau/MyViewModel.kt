package m1.pmob.veget_eau

import android.app.Application
import android.content.Context
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MyViewModel(application: Application): AndroidViewModel(application) {
    val dao = PlantsDatabase.getDatabase(application).myDao()
    //pas besoin de recuppere elt de la liste
    val plantes = dao.getAllPlants()
    //pour recherche plabtes avec prefixes
    var certainesPlantes = MutableLiveData<List<Eplante>>()

    private val context = getApplication<Application>().applicationContext


    fun addPlantes(n: String?, ns: String?, uri: String?){
        Thread{
            if(n!="" || ns!=""){
                Log.d("getPlante", "enregistrer")
                dao.ajoutPlante(Eplante(nomverna = n?.trim(),nomscient = ns?.trim(), uri = uri?.trim()))
            }else{
                Log.d("getPlante", "non enregistrer")
                val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(50)

            }
        }.start()
    }

    fun getPaysPrefix(p:String){
        Log.d("getPlante", "p=$p")
        Thread {
            Log.d("getPlante", "${dao.loadPartialName(p)}")
            certainesPlantes.postValue(dao.loadPartialName(p))
        }.start()

    }

}