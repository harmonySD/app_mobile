package m1.pmob.veget_eau

import android.content.Context
import android.icu.text.DateFormat
import androidx.core.util.rangeTo
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.MonthDay.now

import java.util.*

class PlanningWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
       val dao =  PlantsDatabase.getDatabase(applicationContext).myDao()
        val lstarros : List<Earrosage> = dao.getArrosageToCheckWater()
        val lstid:List<Int> =List<Int>(0) { 0 }
        Calendar.getInstance().time
        // pour savoir si la date d'aujourd'hui est dans l'intervalle des arrosages
        // prendre le nombre de secondes du premier janvier  de l'année courante
        // lui soustraire le nombre de secondes du premier janvier 2000 (toutes nos dates sont en 2000)
        // on a maintenant le nombre de secondes séparant deux jours d'années // problème 29 février ?
        // on soustrait ce nombre de secondes à la date d'aujourd'hui
        // (mode normal) de là si date de début < date de fin
        // (mode inversé) de là si date de début > date de fin

        // (mode normal)  il faut arroser la plante si on se trouve DANS l'intervalle  date début <= (date actuelle - x) <= date fin
            // ET datedebut mod 86400*freqarrosage == dateactuelle -x mod 86400*freqarrosage
       //   il faut arroser la plante si on ne se trouve PAS l'intervalle  date début (date actuelle - x) =< date fin
            // ET datefin mod 86400*freqarrosage == dateactuelle -x mod 86400*freqarrosage
        for (e in lstarros){


       }
        // récupérer toutes les fréquences de toutes les plantes qui sont marqués hasToBeWatered a false
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