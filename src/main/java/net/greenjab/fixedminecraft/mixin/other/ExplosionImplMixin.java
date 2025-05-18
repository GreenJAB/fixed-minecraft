package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionImplMixin {
    @Shadow @Final private Explosion.DestructionType destructionType;

    @Inject(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private void setSurroundingMoraine(CallbackInfo ci, @Local Entity entity) {
        if (this.destructionType == Explosion.DestructionType.TRIGGER_BLOCK) {
            if (entity instanceof FallingBlockEntity FBE) {
                if (FBE.getBlockState().isOf(Blocks.GRAVEL)) {
                    FBE.addCommandTag("convert");
                }
            }
        }
    }
}
