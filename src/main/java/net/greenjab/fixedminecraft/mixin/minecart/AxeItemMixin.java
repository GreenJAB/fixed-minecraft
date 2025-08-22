package net.greenjab.fixedminecraft.mixin.minecart;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Inject(method = "tryStrip", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;syncWorldEvent(Lnet/minecraft/entity/Entity;ILnet/minecraft/util/math/BlockPos;I)V", ordinal = 0
    ))
    private void addCopperRails(World world, BlockPos pos, @Nullable PlayerEntity player,
                                       BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (world instanceof ServerWorld serverWorld && state.isFullCube(world, pos) && world.random.nextFloat()<0.3f) {
            Identifier lootTableId = FixedMinecraft.id("gameplay/other/scrape");
            Block.generateBlockInteractLoot(
                    serverWorld,
                    RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId),
                    state,
                    world.getBlockEntity(pos),
                    null,
                    player,
                     (worldx, stack) -> Block.dropStack(worldx, pos, stack)
            );
        }
    }
}
