package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyExpressionValue(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getExperienceToDrop(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)I"))
    private int nightXP(int original){
        LivingEntity LE = (LivingEntity) (Object)this;
        if (LE.getCommandTags().contains("Night")) {
            return (int)(Math.ceil(original*1.5f));
        }
        return original;
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateKilledAdvancementCriterion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void tntAdvancement(DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getSource() instanceof TntEntity) {
            if ((LivingEntity)(Object)this instanceof HostileEntity) {
                Entity player = damageSource.getAttacker();
                if (player != null) {
                    if (player instanceof ServerPlayerEntity SPE) {
                        Criteria.CONSUME_ITEM.trigger(SPE, Items.TNT.getDefaultStack());
                    }
                }
            }
        }
    }
}
