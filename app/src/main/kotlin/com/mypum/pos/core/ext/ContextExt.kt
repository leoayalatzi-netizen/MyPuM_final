package com.mypum.pos.core.ext
import android.content.Context
fun Context.appName()=applicationInfo.loadLabel(packageManager).toString()