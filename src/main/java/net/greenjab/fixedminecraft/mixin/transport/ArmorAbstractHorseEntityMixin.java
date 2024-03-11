package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.items.ItemRegistry;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents the horse wearing netherite armor from getting into angry pose when taking damage.
 */
@Mixin(AbstractHorseEntity.class)
public class ArmorAbstractHorseEntityMixin {
    @Shadow
    protected SimpleInventory items;

    @Inject(method = "updateAnger", at = @At("HEAD"), cancellable = true)
    private void rejectAngryWhenDrip(CallbackInfo ci) {
        ItemStack armor = items.getStack(1);
        if (armor.getItem() == ItemRegistry.INSTANCE.getNETHERITE_HORSE_ARMOR())
            ci.cancel();
    }
}
