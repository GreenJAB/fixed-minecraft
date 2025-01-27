package net.greenjab.fixedminecraft.mixin.redstone;

import net.greenjab.fixedminecraft.data.ModTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    @Inject(method = "onExploded", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private void oreDrops(BlockState state, ServerWorld serverWorld, BlockPos pos, Explosion explosion,
                          BiConsumer<ItemStack, BlockPos> stackMerger, CallbackInfo ci){
        if (state.isIn(ModTags.INSTANCE.getORES())){
            if (serverWorld.random.nextBoolean()) {
                Block block = state.getBlock();
                boolean bl = explosion.getCausingEntity() instanceof PlayerEntity;
                if (block.shouldDropItemsOnExplosion(explosion)) {
                    BlockEntity blockEntity = state.hasBlockEntity() ? serverWorld.getBlockEntity(pos) : null;
                    LootWorldContext.Builder builder = new LootWorldContext.Builder(serverWorld)
                            .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                            .add(LootContextParameters.TOOL, ItemStack.EMPTY)
                            .addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity)
                            .addOptional(LootContextParameters.THIS_ENTITY, explosion.getEntity());
                    if (explosion.getDestructionType() == Explosion.DestructionType.DESTROY_WITH_DECAY) {
                        builder.add(LootContextParameters.EXPLOSION_RADIUS, explosion.getPower());
                    }

                    state.onStacksDropped(serverWorld, pos, ItemStack.EMPTY, bl);
                    state.getDroppedStacks(builder).forEach(/* method_55224 */ stack -> stackMerger.accept(stack, pos));
                }
            }
        }
    }
}
