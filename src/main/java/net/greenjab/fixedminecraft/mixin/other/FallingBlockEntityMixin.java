package net.greenjab.fixedminecraft.mixin.other;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
}
