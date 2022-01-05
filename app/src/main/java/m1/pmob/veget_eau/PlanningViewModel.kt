package m1.pmob.veget_eau

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlin.concurrent.thread

class PlanningViewModel(application: Application) : AndroidViewModel(application) {
    val applicat = application
    val dao = PlantsDatabase.getDatabase(application).myDao()
    val plantsToWater = MutableLiveData<List<Eplante>>()

    init {
        Thread{
            // on vérifie si des plantes ont le besoin d'être arrosés
            PlanningWorker.checkWater(applicat.applicationContext)
            // on récupère les plantes à arroser.
            plantsToWater.postValue(dao.getPlantsToWater())
        }.start()
    }


    fun setWatered(id: Long) {
        // pour signaler qu'une plante a été arrosée
        Thread {
            // on signale que la plante a été arrosée
            dao.setWater(id, true)
            // on met à jour la liste des plantes qu'il reste à arroser
            plantsToWater.postValue(dao.getPlantsToWater())
        }.start()
    }

    fun addExceptionalWater(inspiration: Earrosage) {
        Thread {

        }.start()
    }


}