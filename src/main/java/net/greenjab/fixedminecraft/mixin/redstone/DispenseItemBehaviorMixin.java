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
import net.minecraft.util.RandomSource;
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
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import static net.greenjab.fixedminecraft.FixedMinecraft.corals;

@Mixin(DispenseItemBehavior.class)
public interface DispenseItemBehaviorMixin {

    @ModifyArg(method="bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DispenserBlock;registerBehavior(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/core/dispenser/DispenseItemBehavior;)V"), slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;POTION:Lnet/minecraft/world/item/Item;",opcode = Opcodes.GETSTATIC),
            to = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;MINECART:Lnet/minecraft/world/item/Item;",opcode = Opcodes.GETSTATIC)), index = 1)
    private static DispenseItemBehavior hydrateCoralDispenser(DispenseItemBehavior behavior) {

        return new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public @NonNull ItemStack execute(@NonNull BlockSource source, @NonNull ItemStack dispensed) {
                PotionContents potion = dispensed.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (!potion.is(Potions.WATER)) {
                    return this.defaultDispenseItemBehavior.dispense(source, dispensed);
                } else {
                    ServerLevel level = source.level();
                    BlockPos pos = source.pos();
                    BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));

                    if (corals.isEmpty()) BlockRegistry.addCoral();
                    BlockState blockState = level.getBlockState(target);
                    if (!(blockState.is(BlockTags.CONVERTABLE_TO_MUD)|| corals.containsKey(blockState.getBlock()))) {
                        return this.defaultDispenseItemBehavior.dispense(source, dispensed);
                    } else {
                        if (!level.isClientSide()) {
                            RandomSource random = level.getRandom();
                            for (int i = 0; i < 5; i++) {
                                level.sendParticles(ParticleTypes.SPLASH,pos.getX() + random.nextDouble(),pos.getY() + 1,pos.getZ() + random.nextDouble(),1,0.0,0.0,0.0,1.0);
                            }
                        }

                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                        if (blockState.is(BlockTags.CONVERTABLE_TO_MUD))
                            level.setBlockAndUpdate(target, Blocks.MUD.defaultBlockState());
                        else {
                            if (blockState.getProperties().contains(BlockStateProperties.WATERLOGGED)) {
                                if (blockState.getProperties().contains(HorizontalDirectionalBlock.FACING))
                                    level.setBlockAndUpdate(target, corals.get(blockState.getBlock()).defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)).setValue(HorizontalDirectionalBlock.FACING, blockState.getValue(HorizontalDirectionalBlock.FACING)));
                                else level.setBlockAndUpdate(target, corals.get(blockState.getBlock()).defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)));
                            } else level.setBlockAndUpdate(target, corals.get(blockState.getBlock()).defaultBlockState());
                        }
                        return this.consumeWithRemainder(source, dispensed, new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
            }
        };
    }

    @ModifyArg(method="bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DispenserBlock;registerBehavior(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/core/dispenser/DispenseItemBehavior;)V"), slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;TNT:Lnet/minecraft/world/level/block/Block;",opcode = Opcodes.GETSTATIC),
            to = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;WITHER_SKELETON_SKULL:Lnet/minecraft/world/item/Item;",opcode = Opcodes.GETSTATIC)), index = 1)
    private static DispenseItemBehavior launchTNTaway(DispenseItemBehavior behavior) {
        return new OptionalDispenseItemBehavior() {
            @Override
            protected @NonNull ItemStack execute(@NonNull BlockSource source, @NonNull ItemStack dispensed) {
                ServerLevel level = source.level();
                if (!level.getGameRules().get(GameRules.TNT_EXPLODES)) {
                    this.setSuccess(false);
                } else {
                    BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                    PrimedTnt tnt = new PrimedTnt(level, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, null);
                    Direction dir = source.state().getValue(DispenserBlock.FACING);
                    if (dir.getAxis() != Direction.Axis.Y){
                        float dis = dir.toYRot();

                        double d = (dis*(Math.PI)/180f)+(level.getRandom().nextDouble() * 0.4 - 0.2);
                        tnt.setDeltaMovement(-Math.sin(d) * 0.02, 0.2F, Math.cos(d) * 0.02);
                    }
                    level.addFreshEntity(tnt);
                    level.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.ENTITY_PLACE, target);
                    dispensed.shrink(1);
                    this.setSuccess(true);
                }
                return dispensed;
            }
        };
    }
}
