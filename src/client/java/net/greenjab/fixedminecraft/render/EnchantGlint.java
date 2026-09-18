package net.greenjab.fixedminecraft.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
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
    public static RenderType getEntityGlint(Identifier texture) {
        if (isSuper()) return GlintRenderLayer.ENTITY_SOLID_GLINT.apply(texture);
        else return RenderTypes.entitySolidGlint(texture);
    }

    @Environment(EnvType.CLIENT)
    public static RenderType getArmorEntityGlint(Identifier texture) {
        if (isSuper()) return GlintRenderLayer.ARMOR_CUTOUT_NO_CULL_GLINT.apply(texture);
        else return RenderTypes.armorCutoutNoCullGlint(texture);
    }

    @Environment(EnvType.CLIENT)
    public static RenderType getTrimmedArmorEntityGlint() {
        if (isSuper()) return GlintRenderLayer.armorEntityGlintColor;
        else return RenderTypes.trimmedArmorGlint();
    }
}
