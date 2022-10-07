package dev.dediamondpro.skyguide.config

import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.annotations.Expose
import dev.dediamondpro.skyguide.SkyGuide
import java.io.*
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.nio.file.Files

object InternalConfig {
    @Transient
     val gson = GsonBuilder()
        .registerTypeAdapter(
            this::class.java,
            InstanceCreator<Any> { _: Type? -> this } as InstanceCreator<*>)
        .excludeFieldsWithoutExposeAnnotation()
        .excludeFieldsWithModifiers(Modifier.TRANSIENT)
        .setPrettyPrinting()
        .create()
    @Transient
    val file = File("./config/${SkyGuide.ID}/internal-config.json")

    fun initialize() {
        if (file.exists()) load()
        else save()
    }

    private fun save() {
        file.parentFile.mkdirs()
        try {
            BufferedWriter(
                OutputStreamWriter(
                    Files.newOutputStream(file.toPath()),
                    StandardCharsets.UTF_8
                )
            ).use { writer ->
                writer.write(
                    gson.toJson(this)
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun load() {
        try {
            BufferedReader(
                InputStreamReader(
                    Files.newInputStream(file.toPath()),
                    StandardCharsets.UTF_8
                )
            ).use { reader ->
                gson.fromJson(
                    reader,
                    this.javaClass
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}