package tw.androchen.splunkcafe.ui.home

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import tw.androchen.splunkcafe.R
import tw.androchen.splunkcafe.util.Scale
import tw.androchen.splunkcafe.util.SplunkHEC

class HomeFragment : Fragment() {

    private lateinit var scaleViewModel: ScaleViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        scaleViewModel =
                ViewModelProvider(this).get(ScaleViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val tvUrl: TextView = root.findViewById(R.id.tv_url)
        val tvToken: TextView = root.findViewById(R.id.tv_token)
        val tvWeight: TextView = root.findViewById(R.id.tv_weight)
        val btnScaleReconnect: Button = root.findViewById(R.id.btn_scale_reconnect)

        tvUrl.text = resources.getString(R.string.tv_splunk_url, SplunkHEC.url)
        tvToken.text = resources.getString(R.string.tv_splunk_token, SplunkHEC.token)

        btnScaleReconnect.setOnClickListener {
            val dialog = ProgressDialog.show(
                activity, "",
                "Scale Connecting. Please wait...", true
            )
            Scale.resume()
            dialog.show()
            scaleViewModel.callScaleInfo().observe(viewLifecycleOwner, Observer {
                tvWeight.text = resources.getString(R.string.tv_scale_weight, it)
                dialog.dismiss()
            })
        }


        return root
    }
}