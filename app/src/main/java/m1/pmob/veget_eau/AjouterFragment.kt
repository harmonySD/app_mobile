package m1.pmob.veget_eau

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import m1.pmob.veget_eau.databinding.FragmentAjouterBinding


class AjouterFragment : Fragment(R.layout.fragment_ajouter) {

    companion object {
        @JvmStatic
        fun newInstance()=AjouterFragment()
    }

    private lateinit var binding : FragmentAjouterBinding
    val model by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAjouterBinding.bind(view)
        binding.bAjouter.setOnClickListener{
            val nc = binding.edNomverna.text.toString().trim()
            val ns = binding.edNomscient.text.toString().trim()
            val uri = binding.edUri.text.toString().trim()
            if(nc=="" && ns==""){
                val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(50,DEFAULT_AMPLITUDE))
                afficherDialog("mettre au moins un nom :(")
                return@setOnClickListener
            }
            model.addPlantes(n=nc,ns=ns,uri=uri)
            binding.edNomscient.text.clear()
            binding.edNomverna.text.clear()
            binding.edUri.text.clear()
            Log.d("kk","enregister")

        }
    }
    fun afficherDialog( s: String ){
        context?.let {
            AlertDialog.Builder(it)
                .setMessage( s )
                .setPositiveButton("OK"){ d, _ -> d.dismiss() }
                .show()
        }
        return
    }

}