package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LlamaFollowCaravanGoal.class)
public abstract class LlamaFollowCaravanGoalMixin {
    @Shadow
    @Final
    public Llama llama;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"))
    private void teleportLlama(CallbackInfo ci, @Local double distanceTo) {
        if (distanceTo > 20) {
            Llama l = this.llama.getCaravanHead();
            assert l != null;
            if (l.onGround()) {
                Vec3 v = l.position();
                this.llama.teleportTo(v.x, v.y, v.z);
                this.llama.fallDistance = 0;
            }
        }
    }
}
