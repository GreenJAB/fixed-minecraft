package net.greenjab.fixedminecraft.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

public class EnchantGlint {

    private static final ThreadLocal<ItemStack> targetStack = new ThreadLocal<>();

    public static void setTargetStack(ItemStack stack) {
        targetStack.set(stack);
    }

    public static boolean isSuper() {
        ItemStack target = targetStack.get();

        if (target == null || target.isEmpty()) return false;
        //return target.getNbt().contains("Super");
        return target.getOrDefault(DataComponentTypes.REPAIR_COST, Integer.valueOf(0)).intValue() == 1;
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

   /* @Environment(EnvType.CLIENT)
    public static RenderLayer getGlintDirect() {
        if (isSuper()) return GlintRenderLayer.glintDirectColor;
        else return RenderLayer.getDirectGlint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderLayer getEntityGlintDirect() {
        if (isSuper()) return GlintRenderLayer.entityGlintDirectColor;
        else return RenderLayer.getDirectEntityGlint();
    }*/

    /*@Environment(EnvType.CLIENT)
    public static RenderLayer getArmorGlint() {
        if (isSuper()) return GlintRenderLayer.armorGlintColor;
        else return RenderLayer.getArmorEntityGlint();
    }*/

    @Environment(EnvType.CLIENT)
    public static RenderLayer getArmorEntityGlint() {
        if (isSuper()) return GlintRenderLayer.armorEntityGlintColor;
        else return RenderLayer.getArmorEntityGlint();
    }
}
