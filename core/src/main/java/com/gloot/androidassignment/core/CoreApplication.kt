package com.gloot.androidassignment.core

import android.app.Application
import com.gloot.androidassignment.core.middlewares.logMiddleware
import com.gloot.androidassignment.core.middlewares.networkMiddleWare
import com.gloot.androidassignment.core.reducers.coreReducer
import org.rekotlin.Store

val store = Store(
        reducer = ::coreReducer,
        state = null,
        middleware = listOf(networkMiddleWare, logMiddleware)
)

class CoreApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @get:Synchronized lateinit var instance: CoreApplication
            private set
    }
}