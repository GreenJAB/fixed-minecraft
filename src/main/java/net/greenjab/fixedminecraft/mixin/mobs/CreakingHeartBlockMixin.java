package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreakingHeartBlock.class)
public abstract class CreakingHeartBlockMixin {
    @Inject(method = "isNightAndNatural", at = @At(value = "HEAD"), cancellable = true)
    private static void spawnInDay(World world, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
        cir.cancel();
    }
}
