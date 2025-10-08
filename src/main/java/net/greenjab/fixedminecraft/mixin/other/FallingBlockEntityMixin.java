package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.block.NewSnowBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {

    @Shadow
    private BlockState blockState;

    @Inject(method = "setHurtEntities", at = @At(value = "HEAD"), cancellable = true)
    private void changeToGravelCancel(float fallHurtAmount, int fallHurtMax, CallbackInfo ci) {
        if (fallHurtMax==-1) {
            this.blockState = Blocks.SAND.getDefaultState();
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void changeToSand(CallbackInfo ci) {
        FallingBlockEntity FBE = (FallingBlockEntity)(Object)this;
        if (FBE.getCommandTags().contains("convert")) {
            World world = FBE.getEntityWorld();
            FallingBlockEntity fallingBlockEntity = EntityType.FALLING_BLOCK.create(FBE.getEntityWorld().getWorldChunk(FBE.getBlockPos()).getWorld(), SpawnReason.CONVERSION);
            fallingBlockEntity.refreshPositionAndAngles(FBE.getX(), FBE.getY(), FBE.getZ(), 0.0F, 0.0F);
            fallingBlockEntity.setVelocity(FBE.getVelocity().x, FBE.getVelocity().y,FBE.getVelocity().z);
            world.spawnEntity(fallingBlockEntity);
            FBE.discard();
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V", ordinal = 3),
            cancellable = true)
    private void fallingSnow(CallbackInfo ci) { if (tryFallingSnow()) ci.cancel();}

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V", ordinal = 4),
            cancellable = true)
    private void fallingSnow2(CallbackInfo ci) { if (tryFallingSnow()) ci.cancel();}

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;dropItem(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;", ordinal = 0),
            cancellable = true)
    private void fallingSnow3(CallbackInfo ci) {
        if (tryFallingSnow()) ci.cancel();}

    @Unique
    private boolean tryFallingSnow() {
        FallingBlockEntity FBE = (FallingBlockEntity)(Object)this;
        if (FBE.getBlockState().isOf(Blocks.SNOW)) {
            Block block = this.blockState.getBlock();
            BlockPos blockPos = FBE.getBlockPos();
            if (FBE.getEntityWorld().getBlockState(blockPos.up()).isOf(Blocks.SNOW)) blockPos = blockPos.up();
            int layers = FBE.getBlockState().get(NewSnowBlock.LAYERS);
            BlockState below = FBE.getEntityWorld().getBlockState(blockPos);
            if (below.isOf(Blocks.SNOW)) {
                int belowLayers = below.get(NewSnowBlock.LAYERS);
                if (layers + belowLayers <= 8) {
                    FBE.getEntityWorld().setBlockState(blockPos, Blocks.SNOW.getDefaultState().with(NewSnowBlock.LAYERS, layers + belowLayers));
                } else {
                    FBE.getEntityWorld().setBlockState(blockPos, Blocks.SNOW.getDefaultState().with(NewSnowBlock.LAYERS, 8));
                    BlockState above = FBE.getEntityWorld().getBlockState(blockPos.up());
                    if (above.isOf(Blocks.AIR)) {
                        FBE.getEntityWorld().setBlockState(blockPos.up(), Blocks.SNOW.getDefaultState().with(NewSnowBlock.LAYERS, layers + belowLayers-8));
                    }
                }
            }
            FBE.discard();
            FBE.onDestroyedOnLanding(block, blockPos);
            return true;
        }
        return false;
    }

}
