package net.greenjab.fixedminecraft.mixin.phantom;

import net.greenjab.fixedminecraft.registry.registries.OtherRegistry;
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;trySleep(Lnet/minecraft/util/math/BlockPos;)Lcom/mojang/datafixers/util/Either;"), cancellable = true)
    private void notTired(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit,
                          CallbackInfoReturnable<ActionResult> cir){
        if (((ServerWorld)world).getGameRules().getValue(GameruleRegistry.Insomnia_Sleep_Requirement)) {
            if (!player.hasStatusEffect(OtherRegistry.INSOMNIA)) {
                player.sendMessage(Text.translatable("block.minecraft.bed.awake"), true);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}
