package net.greenjab.fixedminecraft.mixin.minecart;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {
    @Inject(method = "evaluateNewBlockState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/AxeItem;spawnSoundAndParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/sounds/SoundEvent;I)V", ordinal = 0
    ))
    private void addScrapedCopper(Level world, BlockPos pos, @Nullable Player player,
                                       BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (world instanceof ServerLevel serverWorld && state.isCollisionShapeFullBlock(world, pos) && world.getRandom().nextFloat()<0.3f) {
            Identifier lootTableId = FixedMinecraft.id("gameplay/other/scrape");
            Block.dropFromBlockInteractLootTable(
                    serverWorld,
                    ResourceKey.create(Registries.LOOT_TABLE, lootTableId),
                    state,
                    world.getBlockEntity(pos),
                    null,
                    player,
                     (worldx, stack) -> Block.popResource(worldx, pos, stack)
            );
        }
    }
}
