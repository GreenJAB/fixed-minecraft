package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.block.NewAmethystBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BulbBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
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
    public static void tryResonate(@Nullable Entity sourceEntity, World world, BlockPos pos, int frequency) {
    }

    @Shadow
    @Final
    public static BooleanProperty WATERLOGGED;

    @Inject(method = "tryResonate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"
    ))
    private static void setAmethystBlockAlreadyResonated(Entity sourceEntity, World world, BlockPos pos, int frequency, CallbackInfo ci, @Local(ordinal = 1) BlockPos pos2){
        world.setBlockState(pos2, world.getBlockState(pos2).with(RedstoneTorchBlock.LIT, true));
    }

    @Inject(method = "setActive", at = @At(
            value = "HEAD"
    ))
    private void resontateBefore(Entity sourceEntity, World world, BlockPos pos, BlockState state, int power, int frequency,
                                 CallbackInfo ci){
        tryResonate(sourceEntity, world, pos, frequency);
    }

    @Inject(method = "setActive", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/SculkSensorBlock;tryResonate(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;I)V"
    ), cancellable = true)
    private void dontResontateAfter(Entity sourceEntity, World world, BlockPos pos, BlockState state, int power, int frequency,
                                 CallbackInfo ci){
        world.emitGameEvent(sourceEntity, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, pos);
        if (!(Boolean)state.get(WATERLOGGED)) {
            world.playSound((PlayerEntity)null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
        }
        ci.cancel();
    }
}
