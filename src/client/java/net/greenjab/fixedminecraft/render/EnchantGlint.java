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

        if (target == null || target.isEmpty())  return false;
        if (target.getComponents().contains(DataComponentTypes.REPAIR_COST)) {
             return target.getComponents().get(DataComponentTypes.REPAIR_COST).intValue() ==1;
        }
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
    public static RenderLayer getArmorEntityGlint() {
        if (isSuper()) return GlintRenderLayer.armorEntityGlintColor;
        else return RenderLayer.getArmorEntityGlint();
    }
}
