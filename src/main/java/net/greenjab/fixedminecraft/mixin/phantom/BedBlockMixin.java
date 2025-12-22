package net.greenjab.fixedminecraft.mixin.phantom;

import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BedBlock;isBedWorking(Lnet/minecraft/world/World;)Z"), cancellable = true)
    private void notTired(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit,
                          CallbackInfoReturnable<ActionResult> cir){
        if (((ServerWorld)world).getGameRules().getBoolean(GameruleRegistry.Insomnia_Sleep_Requirement)) {
            if (!player.hasStatusEffect(StatusRegistry.INSOMNIA)) {
                player.sendMessage(Text.translatable("block.minecraft.bed.awake"), true);
                if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                    serverPlayerEntity.setSpawnPoint(serverPlayerEntity.getWorld().getRegistryKey(), pos, serverPlayerEntity.getYaw(), false, true);
                }

                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}
