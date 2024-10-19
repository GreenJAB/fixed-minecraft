package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {


    @Inject(method = "isSmallerThanBoat", at = @At("HEAD"), cancellable = true)
    private void horseInBoat(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof AbstractHorseEntity) {
            cir.setReturnValue(true);
        }
    }
}
