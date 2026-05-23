package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionBrewing.class)
public abstract class PotionBrewingMixin {

    @Inject(method = "addVanillaMixes", at = @At("TAIL"))
    private static void registerPotion(PotionBrewing.Builder builder, CallbackInfo ci){
        builder.addMix(Potions.AWKWARD, Items.SHULKER_SHELL, ItemRegistry.LEVITATION);
    }

    @ModifyArg(method = "addVanillaMixes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionBrewing$Builder;addMix(Lnet/minecraft/core/Holder;Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)V", ordinal = 22))
    private static Item nautilusShellWaterBreathing(Item ingredient) {
        return Items.NAUTILUS_SHELL;
    }

}
