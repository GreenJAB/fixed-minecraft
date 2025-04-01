package net.greenjab.fixedminecraft.mixin.other;

import net.minecraft.block.FlowerPotBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin {

    @Redirect(method = "randomTick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/CreakingHeartBlock;isNightAndNatural(Lnet/minecraft/world/World;)Z"
    ))
    private boolean idNight(World world) {
        return world.isNight() && world.getDimension().natural();
    }
}
