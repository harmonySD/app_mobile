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


    //TODO il faudra sûrement avoir à changer le type de AjouterFragment vers une interface ou quoi ?
    var fragment: AjouterFragment? = null // le fragment doit s'auto enregistrer pour qu'on puisse accéder à son contexte

    fun setfragment(frag:AjouterFragment){this.fragment = frag
    }

    fun callvibrate(){

        if((this.fragment?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.DESTROYED))== false  ){
            this.fragment?.vibrate()
        }
    }
    fun addPlantes(n: String?, ns: String?, uri: String?){
        Thread{
            if(n!="" || ns!=""){
                //Log.d("getPlante", "enregistré)
                dao.ajoutPlante(Eplante(nomverna = n?.trim(),nomscient = ns?.trim(), uri = uri?.trim()))
            }else{
                //Log.d("getPlante", "non enregistré")
                //val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                //vibrator.vibrate(VibrationEffect.createOneShot(50,DEFAULT_AMPLITUDE))
            callvibrate()
            }
        }.start()
    }

    fun getPaysPrefix(p:String){
        //Log.d("getPlante", "p=$p")
        Thread {
            Log.d("getPlante", "${dao.loadPartialName(p)}")
            certainesPlantes.postValue(dao.loadPartialName(p))
        }.start()

    }

}