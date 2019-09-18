package sk.lukasanda.clipit.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.util.Patterns
import androidx.core.app.NotificationCompat
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject
import sk.lukasanda.clipit.R
import sk.lukasanda.clipit.data.db.dao.CategoryDao
import sk.lukasanda.clipit.data.db.dao.ClipboardDao
import sk.lukasanda.clipit.data.db.entity.Category
import sk.lukasanda.clipit.data.db.entity.ClipboardEntry
import sk.lukasanda.clipit.data.db.entity.DBHolder.AssignedCategory
import sk.lukasanda.clipit.utils.Category.APP
import sk.lukasanda.clipit.utils.Category.MISC
import sk.lukasanda.clipit.utils.Category.TYPE
import sk.lukasanda.clipit.utils.shouldGiveCategory
import sk.lukasanda.clipit.utils.with
import sk.lukasanda.clipit.view.main.MainActivity
import java.util.regex.Pattern

class ClipboardService : Service(), KoinComponent {

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"

        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
    }

    private val clipboardDao: ClipboardDao by inject()
    private val categoryDao: CategoryDao by inject()
    private var clipboard: ClipboardManager? = null
    private val disposable = CompositeDisposable()

    private val listener = ClipboardManager.OnPrimaryClipChangedListener {
        //        if (MyApplication.isMainActivityVisible) return@OnPrimaryClipChangedListener
        val text = getClipboard()
        val chain = Single.fromCallable {
            clipboardDao.insert(ClipboardEntry(clipboard = text))
        }.flatMapCompletable {
            if (shouldGiveCategory(
                    text,
                    listOf(
                        "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}",
                        "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
                        Patterns.WEB_URL.pattern()
                    )
                ) && shouldGiveCategory(
                    getClipboardDetail(),
                    listOf("^(?=\\s*\\S).*$")
                )
            ) {
                Completable.fromAction {
                    clipboardDao.insertAssignedCategory(AssignedCategory("Unfiled", it))
                }
            } else {
                insertIfMatches(
                    getClipboard(),
                    "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
                    "Email",
                    it,
                    sk.lukasanda.clipit.utils.getColor(TYPE)
                )
                    .concatWith(
                        insertIfMatches(
                            getClipboard(),
                            Patterns.WEB_URL.pattern(),
                            "Link",
                            it,
                            sk.lukasanda.clipit.utils.getColor(TYPE)
                        )
                    )
                    .concatWith(
                        insertIfMatches(
                            getClipboard(),
                            "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}",
                            "Phone",
                            it,
                            sk.lukasanda.clipit.utils.getColor(TYPE)
                        )
                    )
                    .concatWith(
                        insertIfMatches(
                            getClipboardDetail(),
                            "^(?=\\s*\\S).*$",
                            getClipboardDetail(),
                            it,
                            sk.lukasanda.clipit.utils.getColor(APP)
                        )
                    )
//                    .concatWith(
//                        insertIfMatches(
//                            getClipboard(),
//                            "(\\u00a9|\\u00ae|[\\u2000-\\u3300]|\\ud83c[\\ud000-\\udfff]|\\ud83d[\\ud000-\\udfff]|\\ud83e[\\ud000-\\udfff])",
//                            "Emoticons",
//                            it,
//                            sk.lukasanda.clipit.utils.getColor(MISC)
//                        )
//                    )
            }
        }

        disposable.add(
            chain
                .with()
                .subscribe({
                    Log.d("TAG", "Success")
                }, {
                    Log.e("Tag", "message", it)
                })
        )
    }

    override fun onCreate() {
        super.onCreate()
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard?.addPrimaryClipChangedListener(listener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_START_FOREGROUND_SERVICE -> {
                    startForegroundService()
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Clipboard monitoring channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val activityIntent = Intent(this, MainActivity::class.java)
            val pendingActivityIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
            val serviceIntent = Intent(this, ClipboardService::class.java)
            serviceIntent.action = ACTION_STOP_FOREGROUND_SERVICE

            val pStopSelf = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            val action = NotificationCompat.Action.Builder(0, "STOP", pStopSelf).build()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Clipboard Monitoring..")
                //.setContentText("Click on stop to monitor")
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(action)
                .setContentIntent(pendingActivityIntent)
                .build()
            startForeground(1, notification)
        }
    }

    private fun stopForegroundService() {
        Log.d("TAG_FOREGROUND_SERVICE", "Stop foreground service.")

        // Stop foreground service and remove the notification.
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
        clipboard?.removePrimaryClipChangedListener(listener)
    }

    private fun getClipboardDetail(): String {
        return clipboard?.primaryClipDescription?.label?.toString() ?: ""
    }

    private fun getClipboard(): String {
        return clipboard?.primaryClip?.getItemAt(0)?.coerceToText(this).toString()
    }

    private fun insertIfMatches(
        what: String,
        regex: String,
        categoryName: String,
        id: Long,
        color: Int
    ): Completable {
        return if (Pattern.compile(regex).matcher(what).find()) {
            Completable.fromAction {
                categoryDao.insertCategory(
                    Category(
                        name = categoryName,
                        color = color
                    )
                )
            }.concatWith(
                Completable.fromAction {
                    clipboardDao.insertAssignedCategory(AssignedCategory(n = categoryName, clipId = id))
                }
            )
        } else {
            Completable.complete()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}