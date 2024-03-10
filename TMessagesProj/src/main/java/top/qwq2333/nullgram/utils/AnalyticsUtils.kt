/*
 * Copyright (C) 2019-2023 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */
package top.qwq2333.nullgram.utils

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.os.Handler
import android.util.Base64
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.channel.AbstractChannelListener
import com.microsoft.appcenter.channel.Channel
import com.microsoft.appcenter.crashes.Crashes
import org.telegram.messenger.BuildConfig
import org.telegram.messenger.BuildVars
import org.telegram.messenger.UserConfig
import java.util.Arrays


object AnalyticsUtils {
    private val appCenterToken = BuildVars.APPCENTER_HASH
    private var isInit = false
    private val isEnabled = BuildConfig.APPLICATION_ID != Arrays.toString(Base64.decode("dG9wLnF3cTIzMzMubnVsbGdyYW0=", Base64.DEFAULT))
    private val patchDeviceListener: Channel.Listener = object : AbstractChannelListener() {
        override fun onPreparedLog(log: com.microsoft.appcenter.ingestion.models.Log, groupName: String, flags: Int) {
            val device = log.device
            device.appVersion = BuildConfig.VERSION_NAME
            device.appBuild = BuildConfig.VERSION_CODE.toString()
        }
    }

    private fun addPatchDeviceListener() {
        try {
            val channelField = AppCenter::class.java.getDeclaredField("mChannel")
            channelField.isAccessible = true
            val channel = (channelField[AppCenter.getInstance()] as Channel)
            channel.addListener(patchDeviceListener)
        } catch (e: ReflectiveOperationException) {
            Log.e("add listener", e)
        }
    }

    private fun patchDevice() {
        try {
            val handlerField = AppCenter::class.java.getDeclaredField("mHandler")
            handlerField.isAccessible = true
            val handler = handlerField[AppCenter.getInstance()] as Handler
            handler.post {
                addPatchDeviceListener()
            }
        } catch (e: ReflectiveOperationException) {
            Log.e("patch device", e)
        }
    }

    @JvmStatic
    fun start(app: Application) {
        return
    }

    @JvmStatic
    fun setUserId(id: Long) {
        return
    }

    @JvmStatic
    fun trackEvent(event: String) {
        return
    }

    @JvmStatic
    fun trackEvent(event: String, map: HashMap<String, String?>?) {
        return
    }

    @JvmStatic
    fun trackCrashes(thr: Throwable) {
        return
    }
}
