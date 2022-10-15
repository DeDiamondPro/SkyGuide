package dev.dediamondpro.skyguide.utils

import gg.essential.universal.UMinecraft
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList


object ItemUtils {
    fun createSkull(uuid: String, texture: String): ItemStack {
        val render = ItemStack(Items.skull, 1, 3)
        val tag = NBTTagCompound()
        val skullOwner = NBTTagCompound()
        val properties = NBTTagCompound()
        val textures = NBTTagList()
        val textureNBT = NBTTagCompound()
        skullOwner.setString("Id", uuid)
        skullOwner.setString("Name", uuid)
        textureNBT.setString("Value", texture)
        textures.appendTag(textureNBT)
        properties.setTag("textures", textures)
        skullOwner.setTag("Properties", properties)
        tag.setTag("SkullOwner", skullOwner)
        render.tagCompound = tag
        return render
    }

    fun drawItemStack(stack: ItemStack?, x: Int, y: Int) {
        if (stack == null) return
        val itemRender = UMinecraft.getMinecraft().renderItem
        RenderHelper.enableGUIStandardItemLighting()
        itemRender.zLevel = -145f
        itemRender.renderItemAndEffectIntoGUI(stack, x, y)
        itemRender.zLevel = 0f
    }
}