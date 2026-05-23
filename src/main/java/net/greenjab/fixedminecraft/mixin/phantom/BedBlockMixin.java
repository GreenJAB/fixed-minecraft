package net.greenjab.fixedminecraft.mixin.phantom;

import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startSleepInBed(Lnet/minecraft/core/BlockPos;)Lcom/mojang/datafixers/util/Either;"), cancellable = true)
    private void notTired(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult,
                          CallbackInfoReturnable<InteractionResult> cir){
        if (((ServerLevel) level).getGameRules().get(GameRuleRegistry.INSOMNIA_SLEEP_REQUIREMENT)) {
            if (!player.hasEffect(MobEffectRegistry.INSOMNIA)) {
                player.sendOverlayMessage(Component.translatable("block.minecraft.bed.awake"));
                if (player instanceof ServerPlayer serverPlayerEntity) {
                    serverPlayerEntity.setRespawnPosition(
                            new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(serverPlayerEntity.level()
                                    .dimension(), pos, serverPlayerEntity.getYRot(), serverPlayerEntity.getXRot()), false),
                            true
                    );
                }

                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
