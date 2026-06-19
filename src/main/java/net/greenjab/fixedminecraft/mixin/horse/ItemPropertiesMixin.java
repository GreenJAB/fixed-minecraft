package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin {

    @ModifyExpressionValue(method = "horseArmor", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item$Properties;component(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Lnet/minecraft/world/item/Item$Properties;"))
    private Item.Properties enchantableHorseArmor(Item.Properties original) {
        return original.enchantable(1);
    }
}
