package m1.pmob.veget_eau

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import m1.pmob.veget_eau.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding : ActivityMainBinding by lazy{ ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val names = listOf("AJOUTER", "CONSULTER", "PLANNING")
        //creation des fragments
        val ajouterFragment = AjouterFragment.newInstance()
        val consulterFragment = ConsulterFragment.newInstance()
        val planningFragment = PlanningFragment.newInstance()
        //le slide
        val pagerAdapter = ScreenSlidePagerAdapter(
            this,
            mutableListOf<Fragment>(ajouterFragment,consulterFragment,planningFragment)
        )
        binding.pager.adapter=pagerAdapter

        setContentView(binding.root)

        TabLayoutMediator(binding.tabLayout,binding.pager) {tab,position -> tab.text=names[position]}
            .attach()
        // on s'assure que chaque jour une vérif est faite de la bd et indique les plantes à arroser
        PlanningWorker.schedule_work(applicationContext) //TODO à déplacer dans le ViewModel et à lancer dans un thread à part
    }
    /*
    CE BOUT DE CODE EST _TRES_ INSPIRE DU SITE OFFICIEL D'ANDROID
    https://developer.android.com/training/notify-user/build-notification#kts
    */
    //TODO il faut déplacer ça dans le ViewModel et le lancer dans un thread à part, pas sur le thread
    // de l'ui !
    private fun createNotificationChannel() {
        // on doit créer un channel de notification pour envoyer des notifications
        // si on en possède déjà un ce n'est pas un problème (cf. docu notifications)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = getString(R.string.notification_channel_id)
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}