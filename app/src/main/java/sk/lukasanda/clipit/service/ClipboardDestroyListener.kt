package sk.lukasanda.clipit.service

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context

class ClipboardDestroyListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, ClipboardService::class.java))
    }
}