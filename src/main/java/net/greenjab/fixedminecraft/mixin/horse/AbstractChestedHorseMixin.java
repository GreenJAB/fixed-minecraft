package net.greenjab.fixedminecraft.mixin.horse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.level.Level;

@Mixin(AbstractChestedHorse.class)
public abstract class AbstractChestedHorseMixin extends AbstractHorse {

    protected AbstractChestedHorseMixin(EntityType<? extends AbstractHorse> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "getInventoryColumns", at = @At("HEAD"), cancellable = true)
    private void muleLessColoumns(CallbackInfoReturnable<Integer> cir){
        if ((AbstractChestedHorse)(Object)this instanceof Mule muleEntity ) {
            cir.setReturnValue(muleEntity.hasChest()?3:0);
            cir.cancel();
        }
    }

    @Inject(method = "randomizeAttributes", at = @At(value = "TAIL"))
    private void randomisedAttributes(RandomSource random, CallbackInfo ci){
        AbstractChestedHorse ADE = (AbstractChestedHorse)(Object)this;

        AttributeInstance attribute = ADE.getAttribute(Attributes.JUMP_STRENGTH);
        Objects.requireNonNull(random);
        Objects.requireNonNull(attribute);
        attribute.setBaseValue(generateJumpStrength(random::nextDouble));

        attribute = ADE.getAttribute(Attributes.MOVEMENT_SPEED);
        Objects.requireNonNull(random);
        Objects.requireNonNull(attribute);
        attribute.setBaseValue(generateSpeed(random::nextDouble));
    }
}
