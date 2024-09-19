package net.greenjab.fixedminecraft.glint

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.ItemStack


object EnchantGlint {
    private val targetStack = ThreadLocal<ItemStack>()

    fun setTargetStack(stack: ItemStack) {
        targetStack.set(stack)
    }

    fun isSuper(): Boolean {
        val target = targetStack.get()

        if (target == null || target.isEmpty || !target.hasNbt()) return false
        if (target.nbt!!.contains("Super")) return true
        return false
    }

    @Environment(EnvType.CLIENT)
    fun getGlint(): RenderLayer {
        return if (isSuper()) GlintRenderLayer.glintColor
        else RenderLayer.getGlint()
    }

    @Environment(EnvType.CLIENT)
    fun getEntityGlint(): RenderLayer {
        return if (isSuper()) GlintRenderLayer.entityGlintColor
        else RenderLayer.getEntityGlint()
    }

    @Environment(EnvType.CLIENT)
    fun getGlintDirect(): RenderLayer {
        return if (isSuper()) GlintRenderLayer.glintDirectColor
        else RenderLayer.getDirectGlint()
    }

    @Environment(EnvType.CLIENT)
    fun getEntityGlintDirect(): RenderLayer {
        return if (isSuper()) GlintRenderLayer.entityGlintDirectColor
        else RenderLayer.getDirectEntityGlint()
    }

    @Environment(EnvType.CLIENT)
    fun getArmorGlint(): RenderLayer {
        return if (isSuper()) GlintRenderLayer.armorGlintColor
        else RenderLayer.getArmorGlint()
    }

    @Environment(EnvType.CLIENT)
    fun getArmorEntityGlint(): RenderLayer {
        return if (isSuper()) GlintRenderLayer.armorEntityGlintColor
        else RenderLayer.getArmorEntityGlint()
    }
}
