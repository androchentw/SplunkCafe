package tw.androchen.splunkcafe

import android.app.Application
import android.content.Context
import androidx.multidex.BuildConfig
import timber.log.Timber

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        private lateinit var instance: BaseApplication
        fun getInstance(): Context {
            return instance
        }
    }
}
