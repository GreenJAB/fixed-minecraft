package net.greenjab.fixedminecraft.mixin.redstone;

import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.greenjab.fixedminecraft.FixedMinecraft.corals;

@Mixin(DispenserBehavior.class)
public interface DispenserBehaviorMixin {

    @ModifyArg(method = "registerDefaults", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/DispenserBlock;registerBehavior(Lnet/minecraft/item/ItemConvertible;Lnet/minecraft/block/dispenser/DispenserBehavior;)V", ordinal = 45
    ), index = 1)
    private static DispenserBehavior hudrateCoralDispenser(DispenserBehavior behavior) {

        return new ItemDispenserBehavior() /* DispenserBehavior$7 */ {
            private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                PotionContentsComponent potionContentsComponent = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
                if (!potionContentsComponent.matches(Potions.WATER)) {
                    return this.fallbackBehavior.dispense(pointer, stack);
                } else {
                    ServerWorld serverWorld = pointer.world();
                    BlockPos blockPos = pointer.pos();
                    BlockPos blockPos2 = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));

                    if (corals.isEmpty()) BlockRegistry.addCoral();
                    BlockState blockState = serverWorld.getBlockState(blockPos2);
                    if (!(blockState.isIn(BlockTags.CONVERTABLE_TO_MUD)|| corals.containsKey(blockState.getBlock()))) {
                        return this.fallbackBehavior.dispense(pointer, stack);
                    } else {
                        if (!serverWorld.isClient()) {
                            for (int i = 0; i < 5; i++) {
                                serverWorld.spawnParticles(
                                        ParticleTypes.SPLASH,
                                        blockPos.getX() + serverWorld.random.nextDouble(),
                                        blockPos.getY() + 1,
                                        blockPos.getZ() + serverWorld.random.nextDouble(),
                                        1,
                                        0.0,
                                        0.0,
                                        0.0,
                                        1.0
                                );
                            }
                        }

                        serverWorld.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        serverWorld.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                        if (blockState.isIn(BlockTags.CONVERTABLE_TO_MUD))
                            serverWorld.setBlockState(blockPos2, Blocks.MUD.getDefaultState());
                        else {
                            if (blockState.getProperties().contains(Properties.WATERLOGGED)) {
                                if (blockState.getProperties().contains(HorizontalFacingBlock.FACING))
                                    serverWorld.setBlockState(blockPos2, corals.get(blockState.getBlock()).getDefaultState().with(Properties.WATERLOGGED, blockState.get(Properties.WATERLOGGED)).with(HorizontalFacingBlock.FACING, blockState.get(HorizontalFacingBlock.FACING)));
                                else serverWorld.setBlockState(blockPos2, corals.get(blockState.getBlock()).getDefaultState().with(Properties.WATERLOGGED, blockState.get(Properties.WATERLOGGED)));
                            } else serverWorld.setBlockState(blockPos2, corals.get(blockState.getBlock()).getDefaultState());
                        }
                        return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
            }
        };
    }

}
