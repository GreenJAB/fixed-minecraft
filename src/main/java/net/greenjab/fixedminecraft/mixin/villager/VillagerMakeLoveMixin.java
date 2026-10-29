package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerMakeLove.class)
public abstract class VillagerMakeLoveMixin {

    @Inject(method = "tryToGiveBirth", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 0))
    private void giveFoodBack(CallbackInfo ci, @Local(ordinal = 0, argsOnly = true)Villager v1, @Local(ordinal = 0, argsOnly = true)Villager v2) {
        v1.foodLevel+=12;
        v2.foodLevel+=12;
    }

    @Inject(method = "tryToGiveBirth", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;release(Lnet/minecraft/core/BlockPos;)Z"))
    private void giveFoodBack2(CallbackInfo ci, @Local(ordinal = 0, argsOnly = true)Villager v1, @Local(ordinal = 0, argsOnly = true)Villager v2) {
        v1.foodLevel+=12;
        v2.foodLevel+=12;
    }
}
