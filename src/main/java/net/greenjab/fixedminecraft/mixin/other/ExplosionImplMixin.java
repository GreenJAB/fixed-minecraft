package net.greenjab.fixedminecraft.mixin.other;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExplosionImpl.class)
public class ExplosionImplMixin {
    @Shadow @Final private Explosion.DestructionType destructionType;

    @Redirect(method = "damageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private void setSurroundingMoraine(Entity entity, Vec3d velocity) {
        if (this.destructionType == Explosion.DestructionType.TRIGGER_BLOCK) {
            if (entity instanceof FallingBlockEntity FBE) {
                if (FBE.getBlockState().isOf(Blocks.GRAVEL)) {
                    FBE.addCommandTag("convert");
                }
            }
        }
        entity.addVelocity(velocity);
    }
}
