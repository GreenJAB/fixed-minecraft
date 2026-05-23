package net.greenjab.fixedminecraft.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

/** Credit: Pepperoni-Jabroni */
public class EnchantGlint {

    private static final ThreadLocal<ItemStack> targetStack = new ThreadLocal<>();

    public static void setTargetStack(ItemStack stack) {
        targetStack.set(stack);
    }

    public static boolean isSuper() {
        ItemStack target = targetStack.get();
        if (target == null || target.isEmpty())  return false;

        if (target.getComponents().has(DataComponents.REPAIR_COST)) {
             return target.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) ==1;
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    public static RenderType getGlint() {
        if (isSuper()) return GlintRenderLayer.glintColor;
        else return RenderTypes.glint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderType getEntityGlint() {
        if (isSuper()) return GlintRenderLayer.entityGlintColor;
        else return RenderTypes.entityGlint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderType getGlintTranslucent() {
        if (isSuper()) return GlintRenderLayer.translucentGlintColor;
        else return RenderTypes.glintTranslucent();
    }

    @Environment(EnvType.CLIENT)
    public static RenderType getArmorEntityGlint() {
        if (isSuper()) return GlintRenderLayer.armorEntityGlintColor;
        else return RenderTypes.armorEntityGlint();
    }


    @Environment(EnvType.CLIENT)
    public static RenderType getGlint(boolean green) {
        if (green) return GlintRenderLayer.glintColor;
        else return RenderTypes.glint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderType getEntityGlint(boolean green) {
        if (green) return GlintRenderLayer.entityGlintColor;
        else return RenderTypes.entityGlint();
    }

    @Environment(EnvType.CLIENT)
    public static RenderType getGlintTranslucent(boolean green) {
        if (green) return GlintRenderLayer.translucentGlintColor;
        else return RenderTypes.glintTranslucent();
    }

}
