package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FormCaravanGoal.class)
public abstract class FormCaravanGoalMixin {
    @Shadow
    @Final
    public LlamaEntity llama;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
    private void teleportLlama(CallbackInfo ci, @Local double d) {
        if (d>20) {
            LlamaEntity l = this.llama.getFollowing();
            assert l != null;
            if (l.isOnGround()) {
                Vec3d v = l.getEntityPos();
                this.llama.requestTeleport(v.x, v.y, v.z);
                this.llama.fallDistance = 0;
            }
        }
    }
}
