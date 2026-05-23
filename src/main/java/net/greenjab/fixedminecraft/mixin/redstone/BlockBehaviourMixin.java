package net.greenjab.fixedminecraft.mixin.redstone;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @Inject(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void oreDrops(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion,
                          BiConsumer<ItemStack, BlockPos> onHit, CallbackInfo ci){
        if (state.is(ModTags.ORES)){
            if (level.getRandom().nextBoolean()) {
                Block block = state.getBlock();
                boolean bl = explosion.getIndirectSourceEntity() instanceof Player;
                if (block.dropFromExplosion(explosion)) {
                    BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
                    LootParams.Builder builder = new LootParams.Builder(level)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                            .withOptionalParameter(LootContextParams.THIS_ENTITY, explosion.getDirectSourceEntity());
                    if (explosion.getBlockInteraction() == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                        builder.withParameter(LootContextParams.EXPLOSION_RADIUS, explosion.radius());
                    }

                    state.spawnAfterBreak(level, pos, ItemStack.EMPTY, bl);
                    state.getDrops(builder).forEach(/* method_55224 */ stack -> onHit.accept(stack, pos));
                }
            }
        }
    }
}
