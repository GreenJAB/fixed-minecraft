package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.block.NewSnowBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Shadow
    private BlockState blockState;

    @Inject(method = "setHurtsEntities", at = @At(value = "HEAD"), cancellable = true)
    private void changeToGravelCancel(float damagePerDistance, int damageMax, CallbackInfo ci) {
        if (damageMax == -1) {
            this.blockState = Blocks.SAND.defaultBlockState();
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void changeToSand(CallbackInfo ci) {
        FallingBlockEntity FBE = (FallingBlockEntity)(Object)this;
        if (FBE.entityTags().contains("convert")) {
            Level world = FBE.level();
            FallingBlockEntity fallingBlockEntity = EntityType.FALLING_BLOCK.create(FBE.level().getChunkAt(FBE.blockPosition()).getLevel(), EntitySpawnReason.CONVERSION);
            if (fallingBlockEntity != null) {
                fallingBlockEntity.snapTo(FBE.getX(), FBE.getY(), FBE.getZ(), 0.0F, 0.0F);
                fallingBlockEntity.setDeltaMovement(FBE.getDeltaMovement().x, FBE.getDeltaMovement().y, FBE.getDeltaMovement().z);
                world.addFreshEntity(fallingBlockEntity);
                FBE.discard();
            }
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;discard()V", ordinal = 3),
            cancellable = true)
    private void fallingSnow(CallbackInfo ci) { if (tryFallingSnow()) ci.cancel();}

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;discard()V", ordinal = 4),
            cancellable = true)
    private void fallingSnow2(CallbackInfo ci) { if (tryFallingSnow()) ci.cancel();}

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;", ordinal = 0),
            cancellable = true)
    private void fallingSnow3(CallbackInfo ci) {
        if (tryFallingSnow()) ci.cancel();}

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canBeReplaced(Lnet/minecraft/world/item/context/BlockPlaceContext;)Z"))
    private boolean fallingSnow4(BlockState instance, BlockPlaceContext itemPlacementContext) {
        FallingBlockEntity FBE = (FallingBlockEntity)(Object)this;
        if ((instance.is(Blocks.SNOW) && FBE.level().getBlockState(itemPlacementContext.getClickedPos()).is(Blocks.SNOW))) {
            return false;
        }
        return instance.canBeReplaced(itemPlacementContext);
    }

    @Unique
    private boolean tryFallingSnow() {
        FallingBlockEntity FBE = (FallingBlockEntity)(Object)this;
        if (FBE.getBlockState().is(Blocks.SNOW)) {
            Block block = this.blockState.getBlock();
            BlockPos blockPos = FBE.blockPosition();
            if (FBE.level().getBlockState(blockPos.above()).is(Blocks.SNOW)) blockPos = blockPos.above();
            int layers = FBE.getBlockState().getValue(NewSnowBlock.LAYERS);
            BlockState below = FBE.level().getBlockState(blockPos);
            if (below.is(Blocks.SNOW)) {
                int belowLayers = below.getValue(NewSnowBlock.LAYERS);
                if (layers + belowLayers <= 8) {
                    FBE.level().setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState().setValue(NewSnowBlock.LAYERS, layers + belowLayers));
                } else {
                    FBE.level().setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState().setValue(NewSnowBlock.LAYERS, 8));
                    BlockState above = FBE.level().getBlockState(blockPos.above());
                    if (above.is(Blocks.AIR)) {
                        FBE.level().setBlockAndUpdate(blockPos.above(), Blocks.SNOW.defaultBlockState().setValue(NewSnowBlock.LAYERS, layers + belowLayers-8));
                    }
                }
            }
            FBE.discard();
            FBE.callOnBrokenAfterFall(block, blockPos);
            return true;
        }
        return false;
    }

}
