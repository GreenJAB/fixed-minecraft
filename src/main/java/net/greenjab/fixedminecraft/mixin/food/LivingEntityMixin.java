package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Shadow public abstract boolean isUsingItem();
    @Shadow public abstract ItemStack getItemInHand(InteractionHand hand);
    @Shadow public abstract InteractionHand getUsedItemHand();
    @Shadow public abstract void releaseUsingItem();

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 5))
    private void eatCancelling(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (!level.getGameRules().get(GameRuleRegistry.EAT_HIT_CANCELLING)) return;
        if (this.isUsingItem() && this.getItemInHand(this.getUsedItemHand()).getComponents().has(DataComponents.FOOD)) {
            LivingEntity LE = (LivingEntity)(Object)this;
            if (LE.level().getDifficulty().getId()>1 && source.getEntity()!=null) this.releaseUsingItem();
        }
    }

    @WrapOperation(method = "getItemBlockingWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/BlocksAttacks;blockDelayTicks()I"))
    private int noShieldDelay(BlocksAttacks instance, Operation<Integer> original){
        return 0;
    }
}
