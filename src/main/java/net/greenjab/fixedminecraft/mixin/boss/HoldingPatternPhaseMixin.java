package net.greenjab.fixedminecraft.mixin.boss;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.phase.HoldingPatternPhase;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(HoldingPatternPhase.class)
public class HoldingPatternPhaseMixin {


    @Inject(method = "tickInRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;getFight()Lnet/minecraft/entity/boss/dragon/EnderDragonFight;", ordinal = 0))
    private void checkForDragonFight(CallbackInfo ci) {
        System.out.println("tick");


    }

    @ModifyConstant(method = "tickInRange", constant = @Constant(intValue = 2))
    private int moreFireballs(int constant){
        return 1;
    }


    @ModifyArg(method = "followPath", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"), index = 1)
    private double lessYRange(double x, @Local Vec3i vec3i){
        Random random = new Random();
        return vec3i.getY() + ((random.nextGaussian()/4.0)+0.5)*20;
    }
}
