package cc.polyfrost.polyblock.utils

import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyStore
import java.security.cert.CertificateFactory

object NetworkUtils {
    fun loadCertificate() {
        val factory = CertificateFactory.getInstance("X.509")
        val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
        keystore.load(
            Files.newInputStream(Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts")),
            null
        )
        this::class.java.getResourceAsStream("/ssl/dediamondpro.dev.der")?.use { input ->
            val buffer = BufferedInputStream(input)
            val certificate = factory.generateCertificate(buffer)
            keystore.setCertificateEntry("*.dediamondpro.dev", certificate)
        }
    }

}