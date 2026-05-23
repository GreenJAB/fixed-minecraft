package net.greenjab.fixedminecraft.mixin.client.particle;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Unique
    AmbientParticle voidParticle= new AmbientParticle(ParticleTypes.WHITE_ASH, 0.118093334F);

    @Inject(method = "doAnimateTick", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/level/block/state/BlockState;isCollisionShapeFullBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
    private void voidParticles(int xt, int yt, int zt, int r, RandomSource animateRandom, Block markerParticleTarget,
                               BlockPos.MutableBlockPos pos, CallbackInfo ci, @Local BlockState state) {
        ClientLevel CL = (ClientLevel) (Object)this;
        if (!state.isCollisionShapeFullBlock(CL, pos)) {
            if (CL.getRandom().nextFloat() < (2*Math.max(FixedMinecraftClient.voidFog, FixedMinecraftClient.paleGardenFog)-0.5) && voidParticle.canSpawn(CL.getRandom())) {
                CL.addParticle(
                        voidParticle.particle(), pos.getX() + CL.getRandom().nextDouble(), pos.getY() + CL.getRandom().nextDouble(), pos.getZ() + CL.getRandom().nextDouble(), 0.0, 0.0, 0.0
                );
            }
        }
    }
}
