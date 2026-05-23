package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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

    @Shadow
    @Final
    public static BooleanProperty WATERLOGGED;

    @Inject(method = "tryResonateVibration", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
    ))
    private static void setAmethystBlockAlreadyResonated(Entity sourceEntity, Level level, BlockPos pos, int vibrationFrequency, CallbackInfo ci,
                                                         @Local(ordinal = 1) BlockPos relativePos) {
        level.setBlockAndUpdate(relativePos, level.getBlockState(relativePos).setValue(RedstoneTorchBlock.LIT, true));
    }

    @Inject(method = "activate", at = @At(
            value = "HEAD"
    ))
    private void resontateBefore(Entity sourceEntity, Level level, BlockPos pos, BlockState state, int calculatedPower, int vibrationFrequency,
                                 CallbackInfo ci){
        tryResonateVibration(sourceEntity, level, pos, vibrationFrequency);
    }

    @Inject(method = "activate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/SculkSensorBlock;tryResonateVibration(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I)V"
    ), cancellable = true)
    private void dontResontateAfter(Entity sourceEntity, Level level, BlockPos pos, BlockState state, int calculatedPower, int vibrationFrequency,
                                    CallbackInfo ci){
        level.gameEvent(sourceEntity, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, pos);
        if (!state.getValue(WATERLOGGED)) {
            level.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.2F + 0.8F);
        }
        ci.cancel();
    }
}
