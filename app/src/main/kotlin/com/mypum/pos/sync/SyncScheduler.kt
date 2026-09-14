package com.mypum.pos.sync
import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
class SyncScheduler(private val context:Context){ fun schedule(){ val req=PeriodicWorkRequestBuilder<SyncWorker>(15,TimeUnit.MINUTES).setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build(); WorkManager.getInstance(context).enqueueUniquePeriodicWork("mypum-sync",ExistingPeriodicWorkPolicy.KEEP,req) } }
