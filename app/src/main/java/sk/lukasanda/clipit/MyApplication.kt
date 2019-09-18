package sk.lukasanda.clipit

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.DEBUG
import sk.lukasanda.clipit.di.roomModule
import sk.lukasanda.clipit.di.viewModelModule
import sk.lukasanda.clipit.view.main.MainActivity

class MyApplication : Application(), ActivityLifecycleCallbacks {
    companion object {
        var isMainActivityVisible = false
    }

    override fun onActivityPaused(activity: Activity?) {
        if (activity is MainActivity)
            isMainActivityVisible = false
    }

    override fun onActivityResumed(activity: Activity?) {
        if (activity is MainActivity)
            isMainActivityVisible = true
    }

    override fun onActivityStarted(activity: Activity?) {
        if (activity is MainActivity)
            isMainActivityVisible = true
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (activity is MainActivity)
            isMainActivityVisible = false
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        if (activity is MainActivity)
            isMainActivityVisible = false
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if (activity is MainActivity)
            isMainActivityVisible = true
    }

    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)

        startKoin {
            androidContext(applicationContext)
            if (BuildConfig.DEBUG) {
                logger(AndroidLogger(DEBUG))
            }
            modules(listOf(roomModule, viewModelModule))
        }

        registerActivityLifecycleCallbacks(this)
    }
}