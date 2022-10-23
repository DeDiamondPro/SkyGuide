package dev.dediamondpro.skyguide.utils

import java.io.File
import java.io.IOException
import java.net.URL
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object NetworkUtils {
    private var sslContext: SSLContext? = null

    init {
        try {
            val skyGuideKeyStore = KeyStore.getInstance("JKS")
            skyGuideKeyStore.load(
                this::class.java.getResourceAsStream("/assets/skyguide/skyguidekeystore.jks"),
                "neuneu".toCharArray()
            )
            sslContext = SSLContext.getInstance("TLS")
            val keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            val trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            keyManager.init(skyGuideKeyStore, null)
            trustManager.init(skyGuideKeyStore)
            sslContext!!.init(keyManager.keyManagers, trustManager.trustManagers, null)
        } catch (e: Exception) {
            println("Failed to load SkyGuide keystore, api requests might not work!")
            e.printStackTrace()
        }
    }

    fun downloadFile(url: String, file: File): Boolean {
        return try {
            val connection = setupConnection(URL(url))
            connection.inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun fetchString(url: String): String? {
        return try {
            val connection = setupConnection(URL(url))
            connection.inputStream.use { input ->
                input.bufferedReader().use { it.readText() }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun setupConnection(url: URL): HttpsURLConnection {
        val con = url.openConnection() as HttpsURLConnection
        if (sslContext != null) con.sslSocketFactory = sslContext!!.socketFactory
        con.setRequestProperty(
            "User-Agent",
            "${dev.dediamondpro.skyguide.SkyGuide.ID}-${dev.dediamondpro.skyguide.SkyGuide.VER}"
        )
        con.connectTimeout = 5000
        con.readTimeout = 5000
        return con
    }
}