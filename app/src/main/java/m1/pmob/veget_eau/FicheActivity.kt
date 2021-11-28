package m1.pmob.veget_eau

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import m1.pmob.veget_eau.databinding.ActivityMainBinding
import m1.pmob.veget_eau.databinding.FicheActivityBinding

class FicheActivity: AppCompatActivity()  {
    val binding : FicheActivityBinding by lazy{ FicheActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val names = listOf("FICHE", "MODIFIER")
        //creation des fragments
        val ficheFragment = FicheFragment.newInstance()
        val modifierFragment = ModifierFragment.newInstance()
        //le slide
        val pagerAdapter = ScreenSlidePagerAdapter(
            this,
            mutableListOf<Fragment>(ficheFragment,modifierFragment)
        )
        binding.pager.adapter=pagerAdapter

        setContentView(binding.root)

        TabLayoutMediator(binding.tabLayout,binding.pager) {tab,position -> tab.text=names[position]}
            .attach()
    }

}