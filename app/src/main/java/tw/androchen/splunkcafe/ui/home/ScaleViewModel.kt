package tw.androchen.splunkcafe.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.androchen.splunkcafe.util.Scale

class ScaleViewModel : ViewModel() {

    private var scaleLiveData = MutableLiveData<Float>()

    fun callScaleInfo():LiveData<Float> {
        Scale.register(object : Scale.OnWeightUpdateEvent {
            override fun onUpdate(weight: Float) {
                scaleLiveData.postValue(weight)
            }
        })
        return scaleLiveData
    }
}