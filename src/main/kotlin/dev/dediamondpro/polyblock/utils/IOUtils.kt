package dev.dediamondpro.polyblock.utils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object IOUtils {
    fun getSha256(file: File): String {
        try {
            FileInputStream(file).use { it ->
                val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
                val buffer = ByteArray(1024)
                var count: Int
                while (it.read(buffer).also { count = it } != -1) {
                    digest.update(buffer, 0, count)
                }
                val digested: ByteArray = digest.digest()
                val sb = StringBuilder()
                for (b in digested) {
                    sb.append(((b.toInt() and 0xff) + 0x100).toString(16).substring(1))
                }
                return sb.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}

fun String.toFile(): File {
    return File(this)
}