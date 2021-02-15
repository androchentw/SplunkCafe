package tw.androchen.splunkcafe.util

import android.annotation.SuppressLint
import android.app.Activity
import com.atomax.android.skaleutils.SkaleHelper
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class Scale private constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var skaleHelper : SkaleHelper
        const val REQUEST_BT_PERMISSION = 9999

        fun setupSkale(activity: Activity) {
            skaleHelper = SkaleHelper(activity)
            SkaleHelper.requestBluetoothPermission(activity, REQUEST_BT_PERMISSION);
            skaleHelper.setListener(object : SkaleHelper.Listener {
                override fun onButtonClicked(id: Int) {
                    // invoked when button on Skale is clicked
                    // 1: circle button = power
                    // 2: square button = tare
                    val isTare = (2 == id)
                    if (isTare) {
                        setScaleId()
                        skaleHelper.tare()
                    }
                    AnLog.d("onButtonClicked=$id, id=$scaleId")
                }

                override fun onWeightUpdate(weight: Float) {
                    // invoked when weight value notified from skale
                    // unit of gram.
                    val df = DecimalFormat("#.#")
                    var weight = df.format(weight).toFloat()
                    if (!isJitter(latestWeight, weight)) {
                        latestWeight = weight
                        sendEvent(weight)
                    };
                }

                override fun onBindRequest() {
                    // if new skale was found, SkaleHelper will auto request bind.
                    // this callback will be invoked.
                    AnLog.d("onBindRequest")
                }

                override fun onBond() {
                    // invoked when pairing completed.
                    AnLog.d("onBond")
                }

                override fun onConnectResult(success: Boolean) {
                    // invoked when connection task done
                    AnLog.d("onConnectResult: $success")
                    setScaleSessionId()
                }

                override fun onDisconnected() {
                    // invoked when skale disconnected
                    AnLog.d("onDisconnected")
                    resetScaleSessionId()
                }

                override fun onBatteryLevelUpdate(level: Int) {
                    // invoked after request battery level
                    AnLog.d("onBatteryLevelUpdate: $level")
                }
            })
        }

        fun checkPermissionRequest(requestCode: Int, permissions: Array<String>,
                                   grantResults: IntArray): Boolean {
            return SkaleHelper.checkPermissionRequest(requestCode, permissions, grantResults)
        }



        private var scaleId = "NA"
        private var scaleSessionId = "NA"
        private var latestWeight = 0.0f

        private fun getScaleId(): String {
            return scaleId
        }

        private fun setScaleId() {
            scaleId = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
        }


        fun getScaleSessionId(): String {
            return scaleSessionId
        }

        private fun setScaleSessionId() {
            scaleSessionId = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
        }

        private fun resetScaleSessionId() {
            scaleSessionId = "NA"
        }

        fun resume() {
            setScaleId()
            skaleHelper.resume()
        }

        private fun isJitter(before: Float, after: Float): Boolean {
            var isJitter = false
            if (abs(before - after) <= 0.2) isJitter = true
            return isJitter;
        }

        private fun sendEvent(weight: Float) {
            val body = SplunkHEC.getMetaBody()
            body.put("id", getScaleId())
            body.put("weight", weight)
            AnLog.dn(body.toString())
            SplunkHEC.sendSplunkHEC(body)
        }
    }
}
