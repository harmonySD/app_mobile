package m1.pmob.veget_eau

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class PlanningWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // récupérer toutes les plantes qui sont marqués hasToBeWatered a false  ou leurs fréquences
            //calculer les fréquences si nécessaires
        // pour chaque plante si la date d'aujoudr'hui correspond // voir directement faire le tri dans la bd ?
        // mettre à True dans la BD
        // schedule travail pour le lendemain
        // lancer une notification pouvant être skippé
            //option pour choisir l'heure du check / de la notif ?
        // penser à rajouter un check à chaque lancement de l'application
        return Result.success()
    }


}