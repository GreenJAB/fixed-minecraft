package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyVariable(method = "refreshDimensions", at = @At(value = "STORE"), ordinal = 1)
    private EntityDimensions smallerHorseInBoat(EntityDimensions newDim){
        Entity E = (Entity)(Object)this;
        if (E instanceof AbstractHorse) {
            if (E.isPassenger()) {
                return EntityDimensions.fixed(newDim.width() * 0.9f, newDim.height());
            }
        }
        return newDim;
    }

    @Inject(method = "resetFallDistance", at = @At("HEAD"))
    private void whenPigsFly(CallbackInfo ci) {
        Entity E = (Entity)(Object)this;
        if (E instanceof Pig PE) {
            if (E.fallDistance > 9.5) {
                if (PE.isVehicle()) {
                    if (PE.getControllingPassenger() instanceof ServerPlayer SPE) {
                        CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.SADDLE.getDefaultInstance());
                    }
                }
            }
        }
    }
}
