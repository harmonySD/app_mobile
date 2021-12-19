package m1.pmob.veget_eau

import android.content.Context
import android.icu.text.DateFormat
import java.util.Date
import androidx.core.util.rangeTo
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder

import androidx.work.Worker

import androidx.work.WorkerParameters


import java.util.*
import java.util.concurrent.TimeUnit

class PlanningWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
       val dao =  PlantsDatabase.getDatabase(applicationContext).myDao()
        val lstarros : List<Earrosage> = dao.getArrosageToCheckWater()
        val lstidtowater:MutableList<Long> = MutableList(0) { 0 }
        val cbegin =Calendar.getInstance()
        val cend = Calendar.getInstance()
        val ctest = Calendar.getInstance() // attention à ne pas modifier, il est réglé sur le temps actuel !
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
            cbegin.timeInMillis=e.deb.time
            cend.timeInMillis=e.fin.time
            if(cbegin <= cend){ // mode normal
                if(ctest in cbegin..cend && // on est dans la période d'arrosage ET
                   cbegin.get(Calendar.DAY_OF_YEAR)%e.interval ==ctest.get(Calendar.DAY_OF_YEAR)%e.interval){
                        // on est sur le bon jour de l'intervalle (rapport à la fréquence )
                        lstidtowater.add(e.idp)
                }
            }else{ // mode spécial cbegin > cend
                if(( ctest !in cbegin..cend)&& // on est dans la période d'arrosage ET
                    cend.get(Calendar.DAY_OF_YEAR)%e.interval ==ctest.get(Calendar.DAY_OF_YEAR)%e.interval){
                    // on est sur le bon jour de l'intervalle rapport à la fréquence
                    lstidtowater.add(e.idp)
                }
            }
       }
        // AJOUTER UNE VERIFICATION DE l'UNICITE DE CHAQUE ELEMENT DE LA LISTE
        // POUR REDUIRE LES OPERATIONS DANS LA BD
        for (id in lstidtowater){ // on met à jour la bd en indiquant  qu'il faut arroser toutes les plantes
            // dont aujourd'hui est un jour d'arrosage.
            dao.setWater(id,true)
        }

        // schedule travail pour le lendemain
        OneTimeWorkRequestBuilder<PlanningWorker>()
        PeriodicWorkRequestBuilder<PlanningWorker>(1,TimeUnit.DAYS)
        // lancer une notification pouvant être skippé
            //option pour choisir l'heure du check / de la notif ?
        // penser à rajouter un check à chaque lancement de l'application ????
        return Result.success()
    }


}