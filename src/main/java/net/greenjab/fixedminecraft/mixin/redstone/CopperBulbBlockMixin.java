package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CopperBulbBlock.class)
public abstract class CopperBulbBlockMixin extends Block {
    public CopperBulbBlockMixin(Properties settings) {
        super(settings);
    }

    @Shadow
    public abstract void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos);

    @Override
    public void tick(@NonNull BlockState state, @NonNull ServerLevel world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        checkAndFlip(state, world, pos);
    }

    @WrapOperation(
            method = {"onPlace", "neighborChanged"}, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/CopperBulbBlock;checkAndFlip(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V"
    )
    )
    private void undoMojankCringe(CopperBulbBlock instance, BlockState state, ServerLevel level, BlockPos pos, Operation<Void> original) {
        int delay = 0;
        if (state.toString().toLowerCase().contains("exposed")) delay = 1;
        if (state.toString().toLowerCase().contains("weathered")) delay = 2;
        if (state.toString().toLowerCase().contains("oxidized")) delay = 3;
        if (delay > 0) {
            level.scheduleTick(pos, instance, delay);
        } else {
            this.checkAndFlip(state, level, pos);
        }
    }
}
