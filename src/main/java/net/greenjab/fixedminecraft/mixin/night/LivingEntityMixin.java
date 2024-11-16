package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract @Nullable LivingEntity getPrimeAdversary();

    @Redirect(method = "dropXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getXpToDrop()I"))
    private int nightXP(LivingEntity instance){
        if (instance.getCommandTags().contains("Night")) {
            return (int)(Math.ceil(instance.getXpToDrop()*1.5f));
        }
        return instance.getXpToDrop();
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateKilledAdvancementCriterion(Lnet/minecraft/entity/Entity;ILnet/minecraft/entity/damage/DamageSource;)V"))
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
