package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin {

    @Shadow
    public static void tryResonateVibration(@Nullable Entity sourceEntity, Level level, BlockPos pos, int vibrationFrequency) {
    }

    @Inject(method = "tryResonateVibration", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
    ))
    private static void setAmethystBlockAlreadyResonated(Entity sourceEntity, Level level, BlockPos pos, int vibrationFrequency, CallbackInfo ci,
                                                         @Local(ordinal = 1) BlockPos relativePos) {
        level.setBlockAndUpdate(relativePos, level.getBlockState(relativePos).setValue(RedstoneTorchBlock.LIT, true));
        level.playSound(null, relativePos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0f, vibrationFrequency);
        if (!level.getBlockState(relativePos.above()).isFaceSturdy(level, relativePos.above(), Direction.DOWN)) ((ServerLevel) level).sendParticles(ParticleTypes.NOTE, relativePos.getX() + 0.5, relativePos.getY() + 1, relativePos.getZ() + 0.5, 0, 1, 0.0, 0.0, vibrationFrequency / 15.0);
    }

    @Inject(method = "activate", at = @At(
            value = "HEAD"
    ))
    private void resonateBefore(Entity sourceEntity, Level level, BlockPos pos, BlockState state, int calculatedPower, int vibrationFrequency,
                                 CallbackInfo ci){
        tryResonateVibration(sourceEntity, level, pos, vibrationFrequency);
    }

    @WrapOperation(method = "activate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/SculkSensorBlock;tryResonateVibration(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I)V"
    ))
    private void dontResonateAfter(Entity sourceEntity, Level level, BlockPos pos, int vibrationFrequency, Operation<Void> original){
    }
}
