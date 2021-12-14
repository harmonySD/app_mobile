package m1.pmob.veget_eau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import m1.pmob.veget_eau.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    /*Ceci est le code de l'activité principale, elle contient  les instances des différents fragments
    *  et permet de passer de l'un à l'autre facilement.
    * */

    val binding : ActivityMainBinding by lazy{ ActivityMainBinding.inflate(layoutInflater) }
    // on gonfle le layout pour pouvoir accéder facilements à l'IU

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // permet entre autres survivre aux rotations d'écrans
        val names = listOf("AJOUTER", "CONSULTER", "PLANNING") // mots clés pour naviguer entres les fragments
        //creation des fragments
        val ajouterFragment = AjouterFragment.newInstance()
        val consulterFragment = ConsulterFragment.newInstance()
        val planningFragment = PlanningFragment.newInstance()
        //le slide pour naviguer entre les fragments
        val pagerAdapter = ScreenSlidePagerAdapter(
            this,
            mutableListOf<Fragment>(ajouterFragment,consulterFragment,planningFragment)
        ) // créé un adapteur pour
        binding.pager.adapter=pagerAdapter // affectation de l'adapteur à l'IU

        setContentView(binding.root) // on lie l'IU à l'activité

        TabLayoutMediator(binding.tabLayout,binding.pager) {tab,position -> tab.text=names[position]}
            .attach() // synchronise le Tablayout avec le gestionnaire des différents fragments pour qu'ils coopèrent
    }
}