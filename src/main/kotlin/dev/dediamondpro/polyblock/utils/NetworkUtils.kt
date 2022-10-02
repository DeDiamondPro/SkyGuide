package dev.dediamondpro.polyblock.utils

import dev.dediamondpro.polyblock.PolyBlock
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLConnection

object NetworkUtils {
    fun downloadFile(url: String, file: File): Boolean {
        return try {
            val connection: URLConnection = setupConnection(URL(url))
            connection.getInputStream().use { input ->
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

    fun setupConnection(url: URL): URLConnection {
        val con = url.openConnection()
        con.setRequestProperty("User-Agent", "${PolyBlock.ID}-${PolyBlock.VER}")
        con.connectTimeout = 5000
        con.readTimeout = 5000
        return con
    }
}