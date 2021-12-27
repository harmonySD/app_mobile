package m1.pmob.veget_eau

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Date
import androidx.core.util.rangeTo
import androidx.lifecycle.ViewModelProvider
import androidx.work.*


import java.util.*
import java.util.concurrent.TimeUnit

class PlanningWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    companion object{
        fun schedule_work(cntxt:Context){
            Log.i("WORKSCHED","Entrée planification d'un travail")
            // cette fonction sert à s'assurer qu'un travail est prévu tout les jours pour vérifier la bd
            // elle essaie simplement d'en ajouter un et si il existe déjà il ne se passera rien.
            // on créé un constructeur pour le travail à réaliser
            val workReqBuilder = PeriodicWorkRequestBuilder<PlanningWorker>(2,TimeUnit.MINUTES) // (1,TimeUnit.DAYS)
            WorkManager.getInstance(cntxt).enqueueUniquePeriodicWork(// on envoie notre travail au système
                cntxt.getString(R.string.WorkRequestID), // un id pour qu' on ai au maxium un seul travail.
                ExistingPeriodicWorkPolicy.KEEP, // il doit normalement déjà exister, auquel cas on ne fait rien.
                workReqBuilder.build() // le travail
            )
        }
    }


    override fun doWork(): Result {
        // override obligatoire,
        // là où se fait tout le travail
        Log.i("WORKDO","Entrée exécution travail")
       val dao =  PlantsDatabase.getDatabase(applicationContext).myDao()
        val lstarros : List<Earrosage> = dao.getArrosageToCheckWater()
        val lstidtowater:MutableList<Long> = MutableList(0) { 0 }
        val cbegin =Calendar.getInstance()
        val cend = Calendar.getInstance()
        val ctest = Calendar.getInstance()

        // Pour savoir si aujourd'hui est un jour où il faut arroser
        // (mode normal)   date de début < date de fin
        // (mode inversé)  date de début > date de fin

        // (mode normal)  il faut arroser la plante si on se trouve DANS l'intervalle  date début <= (date actuelle ) <= date fin
            // ET  numerojour(datedebut) mod freqarrosage == numerojour(dateactuelle) mod freqarrosage
       // (mode inversé ) il faut arroser la plante si on ne se trouve PAS l'intervalle  date début <= date actuelle =< date fin
            // ET numerojour(datefin) mod freqarrosage == nbjour(dateactuelle) mod freqarrosage
        //TODO trouver pourquoi il ne détecte pas quand on est dans un jour où il faut arroser
        for (e in lstarros){
            cbegin.timeInMillis=e.deb.time
            cend.timeInMillis=e.fin.time
            if(cbegin <= cend){ // mode normal
                Log.i("WORKDOTIME","mode de temps normal")
                if(ctest in cbegin..cend && // on est dans la période d'arrosage ET
                   cbegin.get(Calendar.DAY_OF_YEAR)%e.interval ==ctest.get(Calendar.DAY_OF_YEAR)%e.interval){
                        // on est sur le bon jour de l'intervalle (rapport à la fréquence )
                        lstidtowater.add(e.idp)
                    Log.i("WORKDOTIMEOK","arrosage detecté avec succès")
                }
            }else{ // mode inversé cbegin > cend
                Log.i("WORKDOTIME","mode de temps inversé")
                if(( ctest !in cbegin..cend) && // on est dans la période d'arrosage ET
                    cend.get(Calendar.DAY_OF_YEAR)%e.interval ==ctest.get(Calendar.DAY_OF_YEAR)%e.interval){
                    // on est sur le bon jour de l'intervalle rapport à la fréquence
                    lstidtowater.add(e.idp)
                    Log.i("WORKDOTIMEOK","arrosage detecté avec succès")
                }
            }
       }

        // TODO AJOUTER UNE VERIFICATION DE l'UNICITE DE CHAQUE ELEMENT DE LA LISTE ?
        // POUR REDUIRE LES OPERATIONS DANS LA BD
        for (id in lstidtowater){ // on met à jour la bd en indiquant  qu'il faut arroser toutes les plantes
            // dont aujourd'hui est un jour d'arrosage.
            dao.setWater(id,true)
        }

        // on s'assure qu'un travail est schedule pour le lendemain.
        schedule_work(applicationContext)
        Log.i("WORKDO","préparation notification")
        //Construction d'une notification pouvant être skippée
        //option pour choisir l'heure du check / de la notif ?
        //TODO asservir l'envoi de notification au fait qu'au moins une plante doit être arrosable
        val notifBuilder = NotificationCompat.Builder(applicationContext,applicationContext.getString(R.string.notification_channel_id))
        notifBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)//sait pas quoi mettre comme icone de notif
        notifBuilder.setContentTitle("Veget\'eau")
        notifBuilder.setContentText("Vous avez des plantes à arroser !")
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // la notification a besoin d'un intent pour savoir quoi faire si on clique dessus
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // on enveloppe l'intent dans un pending pour pouvoir l'envoyer à travers le système Android
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        notifBuilder.setContentIntent(pendingIntent)  //on fournit au constructeur de notification le pending intent
        notifBuilder.setAutoCancel(true)// on peut skipper la notification
        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(2, notifBuilder.build()) //TODO trouver pourquoi il ne lance pas la notification !
        }

        // penser à rajouter un check à chaque lancement de l'application ????
        return Result.success()
    }

}