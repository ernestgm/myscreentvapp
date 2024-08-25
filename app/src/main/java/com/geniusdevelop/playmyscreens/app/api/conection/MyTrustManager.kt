package com.geniusdevelop.playmyscreens.app.api.conection

import android.net.http.X509TrustManagerExtensions
import io.ktor.network.tls.TLSConfigBuilder
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

object MyTrustManager: X509TrustManager {
    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) { }

    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) { }

    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
}