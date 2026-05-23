package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void railAdvancement(CallbackInfo ci){
        Player PE = (Player)(Object)this;
        if (PE.isPassenger()) {
            if (PE.getVehicle() instanceof Minecart) {
                if (PE instanceof ServerPlayer SPE) {
                    int dist = SPE.getStats().getValue(Stats.CUSTOM.get(Stats.MINECART_ONE_CM));
                    if (dist >100000 && dist < 110000) {
                        CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.RAIL.getDefaultInstance());
                    }
                }
            }
        }
    }
}
