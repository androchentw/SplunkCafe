package tw.androchen.splunkcafe.util

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import tw.androchen.splunkcafe.BaseApplication
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SplunkHEC {
    companion object {
        // private const val host = "http://10.0.2.2"       // emulator localhost
        private const val host = "http://192.168.0.190"     // smartphone localhost
        private const val url = "$host:8088/services/collector/raw"
        private const val token = "71d16413-38e9-45fc-8240-2d0417dab977"

        fun getMetaBody(): JSONObject {
            val body = JSONObject()
            val time = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
            body.put("createdTime", time)
            body.put("scaleSessionId", Scale.getScaleSessionId())
            return body;
        }

        fun sendSplunkHEC(message: String?) {
            if (message == null) return
            val body = getMetaBody()
            body.put("message", message)
            sendSplunkHEC(body)
        }

        fun sendSplunkHEC(body: JSONObject) {
            val request = object : JsonObjectRequest(
                Request.Method.POST, url, body,
                Response.Listener<JSONObject> { response ->
                    // AnLog.dn(response.toString())
                },
                Response.ErrorListener { error ->
                    AnLog.e(error)
                }) {
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["Authorization"] = "Splunk $token"
                    return headers
                }
            }
            AnRequest.getInstance(BaseApplication.getInstance()).addToRequestQueue(request)
        }
    }
}
