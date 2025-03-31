package net.greenjab.fixedminecraft.mixin.other;

import net.minecraft.block.EyeblossomBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EyeblossomBlock.class)
public class EyeblossomBlockMixin {

    @Redirect(method = "updateStateAndNotifyOthers", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/CreakingHeartBlock;isNightAndNatural(Lnet/minecraft/world/World;)Z"
    ))
    private boolean isNight(World world) {
        return world.isNightAndNatural();
    }
}
