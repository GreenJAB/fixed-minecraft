package net.greenjab.fixedminecraft.mixin.redstone;

import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.greenjab.fixedminecraft.FixedMinecraft.corals;

@Mixin(DispenseItemBehavior.class)
public interface DispenseItemBehaviorMixin {

    @ModifyArg(method = "bootStrap", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/DispenserBlock;registerBehavior(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/core/dispenser/DispenseItemBehavior;)V", ordinal = 45
    ), index = 1)
    private static DispenseItemBehavior hydrateCoralDispenser(DispenseItemBehavior behavior) {

        return new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior fallbackBehavior = new DefaultDispenseItemBehavior();

            @Override
            public @NonNull ItemStack execute(@NonNull BlockSource pointer, @NonNull ItemStack stack) {
                PotionContents potionContentsComponent = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (!potionContentsComponent.is(Potions.WATER)) {
                    return this.fallbackBehavior.dispense(pointer, stack);
                } else {
                    ServerLevel serverWorld = pointer.level();
                    BlockPos blockPos = pointer.pos();
                    BlockPos blockPos2 = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));

                    if (corals.isEmpty()) BlockRegistry.addCoral();
                    BlockState blockState = serverWorld.getBlockState(blockPos2);
                    if (!(blockState.is(BlockTags.CONVERTABLE_TO_MUD)|| corals.containsKey(blockState.getBlock()))) {
                        return this.fallbackBehavior.dispense(pointer, stack);
                    } else {
                        if (!serverWorld.isClientSide()) {
                            for (int i = 0; i < 5; i++) {
                                serverWorld.sendParticles(
                                        ParticleTypes.SPLASH,
                                        blockPos.getX() + serverWorld.getRandom().nextDouble(),
                                        blockPos.getY() + 1,
                                        blockPos.getZ() + serverWorld.getRandom().nextDouble(),
                                        1,
                                        0.0,
                                        0.0,
                                        0.0,
                                        1.0
                                );
                            }
                        }

                        serverWorld.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        serverWorld.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                        if (blockState.is(BlockTags.CONVERTABLE_TO_MUD))
                            serverWorld.setBlockAndUpdate(blockPos2, Blocks.MUD.defaultBlockState());
                        else {
                            if (blockState.getProperties().contains(BlockStateProperties.WATERLOGGED)) {
                                if (blockState.getProperties().contains(HorizontalDirectionalBlock.FACING))
                                    serverWorld.setBlockAndUpdate(blockPos2, corals.get(blockState.getBlock()).defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)).setValue(HorizontalDirectionalBlock.FACING, blockState.getValue(HorizontalDirectionalBlock.FACING)));
                                else serverWorld.setBlockAndUpdate(blockPos2, corals.get(blockState.getBlock()).defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)));
                            } else serverWorld.setBlockAndUpdate(blockPos2, corals.get(blockState.getBlock()).defaultBlockState());
                        }
                        return this.consumeWithRemainder(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
            }
        };
    }

    @ModifyArg(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DispenserBlock;registerBehavior(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/core/dispenser/DispenseItemBehavior;)V", ordinal = 35), index = 1)
    private static DispenseItemBehavior launchTNTaway(DispenseItemBehavior behavior) {
        return new OptionalDispenseItemBehavior() {
            @Override
            protected @NonNull ItemStack execute(@NonNull BlockSource pointer, @NonNull ItemStack stack) {
                ServerLevel serverWorld = pointer.level();
                if (!serverWorld.getGameRules().get(GameRules.TNT_EXPLODES)) {
                    this.setSuccess(false);
                } else {
                    BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                    PrimedTnt tntEntity = new PrimedTnt(serverWorld, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, null);
                    Direction dir = pointer.state().getValue(DispenserBlock.FACING);
                    if (dir.getAxis() != Direction.Axis.Y){
                        float dis = dir.toYRot();

                        double d = (dis*(Math.PI)/180f)+(serverWorld.getRandom().nextDouble()*0.4-0.2);
                        tntEntity.setDeltaMovement(-Math.sin(d) * 0.02, 0.2F, Math.cos(d) * 0.02);
                    }
                    serverWorld.addFreshEntity(tntEntity);
                    serverWorld.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    serverWorld.gameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
                    stack.shrink(1);
                    this.setSuccess(true);
                }
                return stack;
            }
        };
    }

}
