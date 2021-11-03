package m1.pmob.veget_eau

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
    }
}