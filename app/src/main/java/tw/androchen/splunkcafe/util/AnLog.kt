package tw.androchen.splunkcafe.util

import timber.log.Timber

class AnLog private constructor()  {

  companion object {
    private const val tag = "AnLog"

    fun dn(message: String?) {
      Timber.tag(tag).d(message)
    }

    fun d(message: String?) {
      Timber.tag(tag).d(message)
      SplunkHEC.sendSplunkHEC(message)
    }

    fun i(message: String?) {
      Timber.tag(tag).d(message)
    }

    fun e(message: String?) {
      Timber.tag(tag).e(message)
      SplunkHEC.sendSplunkHEC("ERROR $message")
    }

    fun e(t: Throwable?) {
      Timber.tag(tag).e(t)
    }
  }
}
