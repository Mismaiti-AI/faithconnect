package com.faithconnect

import android.app.Application
import co.touchlab.kermit.Logger

class FaithConnectApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.withTag("FaithConnectApp").d("onCreate")
    }
}