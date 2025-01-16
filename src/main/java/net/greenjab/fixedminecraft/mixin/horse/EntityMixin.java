package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyVariable(method = "calculateDimensions", at = @At(value = "STORE"), ordinal = 1)
    private EntityDimensions smallerHorseInBoat(EntityDimensions original){
        Entity E = (Entity)(Object)this;
        if (E instanceof AbstractHorseEntity) {
            if (E.hasVehicle()) {
                return EntityDimensions.fixed(original.width() * 0.9f, original.height());
            }
        }

        return original;
    }

    @Inject(method = "onLanding", at = @At("HEAD"))
    private void whenPigsFly(CallbackInfo ci) {
        Entity E = (Entity)(Object)this;
        if (E instanceof PigEntity PE) {
            if (E.fallDistance > 9.5) {
                if (PE.hasPassengers()) {
                    if (PE.getControllingPassenger() instanceof ServerPlayerEntity SPE) {
                        Criteria.CONSUME_ITEM.trigger(SPE, Items.SADDLE.getDefaultStack());
                    }
                }
            }
        }
    }
}
