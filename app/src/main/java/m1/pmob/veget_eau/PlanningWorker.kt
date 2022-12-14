package m1.pmob.veget_eau

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock.sleep
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*


import java.util.*
import java.util.concurrent.TimeUnit

class PlanningWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    companion object {
        fun schedule_work(cntxt: Context) {
            // cette fonction sert à s'assurer qu'un travail est prévu tout les jours pour vérifier la bd
            // elle essaie simplement d'en ajouter un et si il existe déjà il ne se passera rien.
            // on créé un constructeur pour le travail à réaliser
            val workReqBuilder = PeriodicWorkRequestBuilder<PlanningWorker>(1, TimeUnit.DAYS)
            WorkManager.getInstance(cntxt)
                .enqueueUniquePeriodicWork(// on envoie notre travail au système
                    cntxt.getString(R.string.WorkRequestID), // un id pour qu' on ai au maxium un seul travail.
                    ExistingPeriodicWorkPolicy.KEEP, // il doit normalement déjà exister, auquel cas on ne fait rien.
                    workReqBuilder.build() // le travail
                )
        }

        fun checkWater(cont: Context): Boolean {
            // fonction statique pour modifier la bd si il y a des plantes à arroser,
            val dao = PlantsDatabase.getDatabase(cont).myDao()
            val lstarros: List<Earrosage> = dao.getArrosageToCheckWater()
            val lstidtowater: MutableList<Long> = MutableList(0) { 0 }
            val cbegin = Calendar.getInstance()
            val cend = Calendar.getInstance()
            val ctest = Calendar.getInstance()

            // Pour savoir si aujourd'hui est un jour où il faut arroser
            // (mode normal)   date de début < date de fin
            // (mode inversé)  date de début > date de fin

            // (mode normal)  il faut arroser la plante si on se trouve DANS l'intervalle  date début <= (date actuelle ) <= date fin
            // ET  numerojour(datedebut) mod freqarrosage == numerojour(dateactuelle) mod freqarrosage
            // (mode inversé ) il faut arroser la plante si on ne se trouve PAS l'intervalle  date début <= date actuelle =< date fin
            // ET numerojour(datefin) mod freqarrosage == nbjour(dateactuelle) mod freqarrosage

            for (e in lstarros) { // on regarde tous les arrosages.
                cbegin.timeInMillis = e.deb.time
                cend.timeInMillis = e.fin.time
                if (cbegin <= cend) { // mode normal
                    if (ctest.get(Calendar.DAY_OF_YEAR) in cbegin.get(Calendar.DAY_OF_YEAR)..cend.get(
                            Calendar.DAY_OF_YEAR
                        ) && // on est dans la période d'arrosage ET
                        cbegin.get(Calendar.DAY_OF_YEAR) % e.interval == ctest.get(Calendar.DAY_OF_YEAR) % e.interval
                    ) {
                        // on est sur le bon jour de l'intervalle (rapport à la fréquence )
                        lstidtowater.add(e.idp)
                    }
                } else { // mode inversé cbegin > cend
                    if (ctest.get(Calendar.DAY_OF_YEAR) !in cbegin.get(Calendar.DAY_OF_YEAR)..cend.get(
                            Calendar.DAY_OF_YEAR
                        ) && // on est dans la période d'arrosage ET
                        cend.get(Calendar.DAY_OF_YEAR) % e.interval == ctest.get(Calendar.DAY_OF_YEAR) % e.interval
                    ) {
                        // on est sur le bon jour de l'intervalle rapport à la fréquence
                        lstidtowater.add(e.idp)
                    }
                }
            }

            for (id in lstidtowater) { // on met à jour la bd en indiquant  qu'il faut arroser toutes les plantes
                // dont aujourd'hui est un jour d'arrosage.
                dao.setWater(id, true)
            }
            return lstidtowater.size > 0
        }

    }


    override fun doWork(): Result {
        for (i in 1..900) { // cette boucle for sert à pouvoir exécuter le work toutes les 2 minutes pour démo
            // override obligatoire,
            // là où se fait tout le travail

            val PlantsNeedWater = checkWater(applicationContext)
            // on s'assure qu'un travail est schedule pour le lendemain.
            schedule_work(applicationContext)

            if (PlantsNeedWater) {// il y avait bien au moins une plante à arroser !
                //Construction d'une notification pouvant être skippée
                //option pour choisir l'heure du check / de la notif ?
                val notifBuilder = NotificationCompat.Builder(
                    applicationContext,
                    applicationContext.getString(R.string.notification_channel_id)
                )
                notifBuilder.setSmallIcon(R.drawable.notif_icon)//sait pas quoi mettre comme icone de notif
                //notifBuilder.setLargeIcon( BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.plante))
                notifBuilder.setContentTitle("Veget\'eau")
                notifBuilder.setContentText("Vous avez des plantes à arroser !")
                notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // la notification a besoin d'un intent pour savoir quoi faire si on clique dessus
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                // on enveloppe l'intent dans un pending pour pouvoir l'envoyer à travers le système Android
                val pendingIntent: PendingIntent =
                    PendingIntent.getActivity(applicationContext, 0, intent, 0)

                //on fournit au constructeur de notification le pending intent
                notifBuilder.setContentIntent(pendingIntent)
                notifBuilder.setAutoCancel(true)// on peut skipper la notification
                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(1, notifBuilder.build())
                }
            }
            sleep(120000) // pour la démo une notification toutes les deux minutes
        }
        return Result.success()
    }
}