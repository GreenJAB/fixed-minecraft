package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin  {



    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void railAdvancement(CallbackInfo ci){
        PlayerEntity PE = (PlayerEntity)(Object)this;
        if (PE.hasVehicle()) {
            if (PE.getVehicle() instanceof MinecartEntity) {
                if (PE instanceof ServerPlayerEntity SPE) {
                    int dist = SPE.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.MINECART_ONE_CM));
                    if (dist >100000 && dist < 110000) {
                        Criteria.CONSUME_ITEM.trigger(SPE, Items.RAIL.getDefaultStack());
                    }
                }
            }
        }
    }
}
