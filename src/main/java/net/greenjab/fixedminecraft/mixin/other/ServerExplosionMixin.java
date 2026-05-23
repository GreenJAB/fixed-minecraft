package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin {
    @Shadow @Final private Explosion.BlockInteraction blockInteraction;

    @Inject(method = "hurtEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(Lnet/minecraft/world/phys/Vec3;)V"))
    private void setSurroundingMoraine(CallbackInfo ci, @Local Entity entity) {
        if (this.blockInteraction == Explosion.BlockInteraction.TRIGGER_BLOCK) {
            if (entity instanceof FallingBlockEntity FBE) {
                if (FBE.getBlockState().is(Blocks.GRAVEL)) {
                    FBE.addTag("convert");
                }
            }
        }
    }
}
