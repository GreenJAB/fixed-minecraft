package net.greenjab.fixedminecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;

public class EnchantGlint {

    private static final ThreadLocal<ItemStack> targetStack = new ThreadLocal<>();

    public static void setTargetStack(ItemStack stack) {
        targetStack.set(stack);
    }

    public static boolean isSuper() {
        ItemStack target = targetStack.get();

        if (target == null || target.isEmpty() || !target.hasNbt()) return false;
        if (target.getNbt().contains("Super")) return true;
        return false;
    }

    @Environment(EnvType.CLIENT)
    public static RenderLayer getGlint() {
        if (isSuper()) return GlintRenderLayer.glintColor;
        else return RenderLayer.getGlint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderLayer getEntityGlint() {
        if (isSuper()) return GlintRenderLayer.entityGlintColor;
        else return RenderLayer.getEntityGlint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderLayer getGlintDirect() {
        if (isSuper()) return GlintRenderLayer.glintDirectColor;
        else return RenderLayer.getDirectGlint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderLayer getEntityGlintDirect() {
        if (isSuper()) return GlintRenderLayer.entityGlintDirectColor;
        else return RenderLayer.getDirectEntityGlint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderLayer getArmorGlint() {
        if (isSuper()) return GlintRenderLayer.armorGlintColor;
        else return RenderLayer.getArmorGlint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderLayer getArmorEntityGlint() {
        if (isSuper()) return GlintRenderLayer.armorEntityGlintColor;
        else return RenderLayer.getArmorEntityGlint();
    }
}
