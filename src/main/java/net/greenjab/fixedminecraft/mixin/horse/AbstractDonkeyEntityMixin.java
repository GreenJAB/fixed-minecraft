package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractDonkeyEntity.class)
public abstract class AbstractDonkeyEntityMixin extends AbstractHorseEntity {

    protected AbstractDonkeyEntityMixin(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getInventoryColumns", at = @At("HEAD"), cancellable = true)
    private void muleLessColoumns(CallbackInfoReturnable<Integer> cir){
        if ((AbstractDonkeyEntity)(Object)this instanceof MuleEntity muleEntity ) {
            cir.setReturnValue(muleEntity.hasChest()?3:0);
            cir.cancel();
        }
    }
}
