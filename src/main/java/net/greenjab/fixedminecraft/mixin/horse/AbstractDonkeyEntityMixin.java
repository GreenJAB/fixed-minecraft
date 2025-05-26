package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

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

    @Inject(method = "initAttributes", at = @At(value = "TAIL"))
    private void randomisedAttributes(Random random, CallbackInfo ci){
        AbstractDonkeyEntity ADE = (AbstractDonkeyEntity)(Object)this;

        EntityAttributeInstance var10000 = ADE.getAttributeInstance(EntityAttributes.JUMP_STRENGTH);
        Objects.requireNonNull(random);
        var10000.setBaseValue(getChildJumpStrengthBonus(random::nextDouble));

        var10000 = ADE.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        Objects.requireNonNull(random);
        var10000.setBaseValue(getChildMovementSpeedBonus(random::nextDouble));
    }
}
