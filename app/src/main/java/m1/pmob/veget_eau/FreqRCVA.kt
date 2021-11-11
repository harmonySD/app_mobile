package m1.pmob.veget_eau

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import m1.pmob.veget_eau.databinding.PlantFreqViewBinding

class FreqRCVA(): RecyclerView.Adapter<VH> (){
    private val lst:List<PlantFreqView> = ArrayList<PlantFreqView>(3)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(PlantFreqView(parent.context))

    }

    override fun onBindViewHolder(holder: VH, position: Int){
    }

    override fun getItemCount(): Int {
        return lst.size
    }

}

class PlantFreqView(c: Context) : View(c) {
    val binding = PlantFreqViewBinding.inflate(LayoutInflater.from(c))
    val freqarros:EditText = binding.freqjour
    val jourdeb:EditText = binding.jourdeb
    val jourfin:EditText = binding.jourfin
    val moisdeb: Spinner = binding.moisdeb
    val moisfin:Spinner = binding.moisfin
    val typearros:Spinner = binding.typearros
}

class VH(v:View) : ViewHolder(v){

}