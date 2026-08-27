package net.greenjab.fixedminecraft.mixin.redstone;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;

import static com.mojang.text2speech.Narrator.LOGGER;

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

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void tryPlaceParrot(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult,
                                CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.isCrouching()) {
            if (hitResult.getDirection() == Direction.UP && state.isFaceSturdy(level, pos, Direction.UP)) {
                if (level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
                    if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()&&
                        player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                        if (player.getShoulderParrotLeft().isPresent()) {
                            respawnEntityOnShoulder(serverPlayer, pos, serverPlayer.getShoulderEntityLeft());
                            serverPlayer.setShoulderEntityLeft(new CompoundTag());
                            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
                        } else if (player.getShoulderParrotRight().isPresent()) {
                            respawnEntityOnShoulder(serverPlayer, pos, serverPlayer.getShoulderEntityRight());
                            serverPlayer.setShoulderEntityRight(new CompoundTag());
                            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
                        }
                    }
                }
            }
        }
    }

    @Unique private void respawnEntityOnShoulder(ServerPlayer serverPlayer, BlockPos pos, final CompoundTag tag) {
        ServerLevel serverLevel = serverPlayer.level();
        if (serverLevel instanceof ServerLevel) {
            if (!tag.isEmpty()) {
                try (ProblemReporter.ScopedCollector reporterx = new ProblemReporter.ScopedCollector(serverPlayer.problemPath(), LOGGER)) {
                    EntityType.create(
                                    TagValueInput.create(reporterx.forChild( () -> ".shoulder"), serverLevel.registryAccess(), tag),
                                    serverLevel,
                                    EntitySpawnReason.LOAD
                            )
                            .ifPresent(entity -> {
                                if (entity instanceof TamableAnimal tamed) {
                                    tamed.setOwner(serverPlayer);
                                    tamed.setOrderedToSit(true);
                                }

                                entity.setPos(pos.getX()+0.5, pos.getY() + 1.0F, pos.getZ()+0.5);
                                serverLevel.addWithUUID(entity);
                            });
                }
            }
        }
    }
}
