package dev.dediamondpro.skyguide.gui

import dev.dediamondpro.skyguide.map.poi.Npc
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.ItemUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import dev.dediamondpro.skyguide.utils.roundTo
import gg.essential.universal.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiTextField
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.awt.Color
import java.net.URI

class NpcGui(private val npcs: MutableList<Npc> = collectedNpcs.toMutableList()) : UScreen() {
    private val json = Json { prettyPrint = true }
    private val finishedNpcs = mutableListOf<Npc>()
    private var npc: Npc? = null
    private val nameTextBox = GuiTextField(0, UMinecraft.getFontRenderer(), 10, 30, 400, 20)
    private val wikiTextBox = GuiTextField(1, UMinecraft.getFontRenderer(), 10, 80, 400, 20)
    private var item: ItemStack? = null

    init {
        collectedNpcs.clear()
        doneEntities.clear()
    }

    override fun initScreen(width: Int, height: Int) {
        nameTextBox.maxStringLength = 200
        wikiTextBox.maxStringLength = 200
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        Gui.drawRect(0, 0, UResolution.scaledWidth, UResolution.scaledHeight, Color(0, 0, 0, 150).rgb)
        if (npc == null) {
            if (npcs.isEmpty()) {
                copyToClipboard()
                GuiUtils.displayScreen(null)
                return
            }
            npc = npcs.removeFirst()
            nameTextBox.text = npc?.name ?: "Unknown"
            wikiTextBox.text = npc?.wiki ?: ""
            if (npc != null) item = ItemUtils.createSkull(npc!!.owner, npc!!.texture)
        }
        val fb = UMinecraft.getFontRenderer()

        fb.drawStringWithShadow("Name:", 10f, 18f, Color.WHITE.rgb)
        nameTextBox.drawTextBox()
        fb.drawStringWithShadow("Wiki Link:", 10f, 68f, Color.WHITE.rgb)
        wikiTextBox.drawTextBox()

        RenderUtils.drawRectBorder(10, 130, fb.getStringWidth("Confirm") + 16, 20, Color.BLACK.rgb, Color.WHITE.rgb)
        fb.drawStringWithShadow("Confirm", 18f, 135.5f, Color.WHITE.rgb)
        RenderUtils.drawRectBorder(100, 130, fb.getStringWidth("Skip") + 16, 20, Color.BLACK.rgb, Color.WHITE.rgb)
        fb.drawStringWithShadow("Skip", 108f, 135.5f, Color.WHITE.rgb)

        UGraphics.GL.pushMatrix()
        UGraphics.GL.translate(420f, 0f, 0f)
        UGraphics.GL.scale(10f, 10f, 1f)
        if (item != null) ItemUtils.drawItemStack(item!!, 0, 0)
        UGraphics.GL.popMatrix()
        fb.drawStringWithShadow(
            "x: ${npc?.x} y: ${npc?.y} z: ${npc?.z}",
            500f - fb.getStringWidth("x: ${npc?.x} y: ${npc?.y} z: ${npc?.z}") / 2f,
            160f,
            Color.WHITE.rgb
        )
    }

    override fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {
        nameTextBox.mouseClicked(mouseX.toInt(), mouseY.toInt(), mouseButton)
        wikiTextBox.mouseClicked(mouseX.toInt(), mouseY.toInt(), mouseButton)
        val fb = UMinecraft.getFontRenderer()
        if (mouseX.toInt() in 10 until fb.getStringWidth("Confirm") + 26 && mouseY.toInt() in 130 until 150) {
            finishedNpcs.add(
                Npc(
                    nameTextBox.text,
                    wikiTextBox.text,
                    npc!!.owner,
                    npc!!.texture,
                    npc!!.x,
                    npc!!.y,
                    npc!!.z
                )
            )
            npc = null
        } else if (mouseX.toInt() in 100 until fb.getStringWidth("Skip") + 116 && mouseY.toInt() in 130 until 150) {
            npc = null
        } else if (mouseX.toInt() in 420 until 580 && mouseY.toInt() in 0 until 160) {
            UDesktop.browse(URI(wikiTextBox.text))
        }
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        if (nameTextBox.isFocused) nameTextBox.textboxKeyTyped(typedChar, keyCode)
        if (wikiTextBox.isFocused) wikiTextBox.textboxKeyTyped(typedChar, keyCode)
        if (!nameTextBox.isFocused && !wikiTextBox.isFocused) super.onKeyPressed(keyCode, typedChar, modifiers)
    }

    override fun onScreenClose() {
        if (finishedNpcs.isEmpty()) return
        copyToClipboard()
    }

    private fun copyToClipboard() {
        UDesktop.setClipboardString(
            if (finishedNpcs.size == 1) {
                json.encodeToString(
                    Json.serializersModule.serializer(),
                    finishedNpcs[0]
                )
            } else {
                json.encodeToString(
                    Json.serializersModule.serializer(),
                    finishedNpcs
                )
            }
        )
    }

    class NpcCollector {
        private val regex = Regex("[a-z0-9]{10}")

        @SubscribeEvent
        fun onTick(event: ClientTickEvent) {
            if (!collectingNpcs || event.phase != TickEvent.Phase.END) return
            for (entity in UMinecraft.getWorld()!!.playerEntities.filter { regex.matches(it.name) }) {
                if (doneEntities.contains(entity)) continue
                val npc = getNpc(entity, true) ?: continue
                doneEntities.add(entity)
                collectedNpcs.add(npc)
            }
        }
    }

    companion object {
        private val doneEntities = mutableListOf<EntityPlayer>()
        private val collectedNpcs = mutableListOf<Npc>()
        var collectingNpcs = false

        fun getNpc(npc: EntityPlayer, checkNameTag: Boolean = false): Npc? {
            for (string in npc.gameProfile.properties.keySet()) {
                for (property in npc.gameProfile.properties.get(string)) {
                    if (property.name == "textures") {
                        val armorStand = UMinecraft.getWorld()!!.loadedEntityList.firstOrNull {
                            EntityArmorStand::class.java.isAssignableFrom(it.javaClass) && it.posX == npc.posX && it.posZ == npc.posZ && it.name.split(
                                '§'
                            ).size <= 2
                        }
                        if (checkNameTag && armorStand?.alwaysRenderNameTag == false) return null
                        val name = armorStand?.name?.replace(Regex("§[a-z0-9]"), "")
                        return Npc(
                            name ?: "",
                            if (name == null) null else "https://wiki.hypixel.net/${
                                name.replace(
                                    ' ',
                                    '_'
                                )
                            }",
                            npc.uniqueID.toString(),
                            property.value,
                            npc.posX.roundTo(1).toFloat(),
                            npc.posY.roundTo(1).toFloat(),
                            npc.posZ.roundTo(1).toFloat()
                        )
                    }
                }
            }
            return null
        }
    }
}