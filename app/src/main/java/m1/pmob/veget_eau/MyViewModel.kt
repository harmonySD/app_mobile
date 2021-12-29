package m1.pmob.veget_eau

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.collections.ArrayList

class MyViewModel(application: Application) : AndroidViewModel(application) {
    val applicat = application
    val dao = PlantsDatabase.getDatabase(application).myDao()

    //pas besoin de recuppere elt de la liste
    val plantes = dao.getAllPlants()

    //pour recherche plabtes avec prefixes
    var certainesPlantes = MutableLiveData<List<Eplante>>()
    var ArrosageToCheck = MutableLiveData<List<Earrosage>>()

    fun addPlantes(n: String, ns: String, uri: String?) {
        Thread {
            dao.ajoutPlante(Eplante(nomverna = n.trim(), nomscient = ns.trim(), uri = uri?.trim()))
        }.start()
    }

    fun addArros(idp: Long, type: Typearros, interval: Int, deb: Date, fin: Date) {
        Thread {
            dao.ajoutArros(
                Earrosage(
                    idp = idp,
                    type = type,
                    interval = interval,
                    deb = deb,
                    fin = fin
                )
            )
        }.start()
    }


    fun getPlantesPrefix(p: String) {
        Thread {
            Log.d("getPlante", "${dao.loadPartialName(p)}")
            certainesPlantes.postValue(dao.loadPartialName(p))
        }.start()
    }

    // cette fonction est normalement inutile car on ne l'utiliserai que dans le workerthread PlanningWorker
    // hors il n'a pas d'UI donc on peut faire les opérations lourdes dedans sans problèmes
    fun getArrosToCheckWater() {
        Thread {
            ArrosageToCheck.postValue(dao.getArrosageToCheckWater())
        }.start()
    }

    fun addPlantesandArros(n: String, ns: String, uri: String?, vararg lstfakearros: Earrosage) {
        Thread {
            val ret: List<Long> = dao.ajoutPlante(
                Eplante(
                    nomverna = n.trim(),
                    nomscient = ns.trim(),
                    uri = uri?.trim()
                )
            )
            for (fakearros in lstfakearros) {
                dao.ajoutArros(
                    Earrosage(
                        idp = ret[0],
                        type = fakearros.type,
                        interval = fakearros.interval,
                        deb = fakearros.deb,
                        fin = fakearros.fin
                    )
                )
            }

        }.start()
    }


    /*
    CE BOUT DE CODE EST _TRES_ INSPIRE DU SITE OFFICIEL D'ANDROID
    https://developer.android.com/training/notify-user/build-notification#kts
    */
    fun setupWorker(){ // créé un canal de notification et s'assure que le work est prévu toutes les 24 heures.
        Thread {
            PlanningWorker.schedule_work(applicat.applicationContext)
            // on doit créer un channel de notification pour envoyer des notifications
            // si on en possède déjà un ce n'est pas un problème (cf. docu notifications)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val id = applicat.applicationContext.getString(R.string.notification_channel_id)
                val name = applicat.applicationContext.getString(R.string.notification_channel_name)
                val descriptionText =
                    applicat.applicationContext.getString(R.string.notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(id, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    applicat.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }.start()
    }

}