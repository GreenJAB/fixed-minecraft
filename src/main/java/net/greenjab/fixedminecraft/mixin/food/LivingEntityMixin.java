package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract ItemStack getStackInHand(Hand hand);

    @Shadow
    public abstract Hand getActiveHand();

    @Shadow
    public abstract void stopUsingItem();

    @Inject(method = "damage", at = @At(value = "INVOKE",
                                        target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 5
    ))
    private void eatCancelling(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.isUsingItem()) {
            if (this.getStackInHand(this.getActiveHand()).getComponents().contains(DataComponentTypes.FOOD)) {
                LivingEntity LE = (LivingEntity)(Object)this;
                if (LE.getEntityWorld().getDifficulty().getId()>1) {
                    if (source.getAttacker()!=null) {
                        this.stopUsingItem();
                    }
                }
            }
        }
    }

    @Redirect(method = "getBlockingItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/BlocksAttacksComponent;getBlockDelayTicks()I"
    ))
    private int noShieldDelay(BlocksAttacksComponent instance){
        return 0;
    }
}
