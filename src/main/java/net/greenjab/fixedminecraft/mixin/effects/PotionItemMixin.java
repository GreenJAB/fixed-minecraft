package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.greenjab.fixedminecraft.FixedMinecraft.corals;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {

    @Inject(method = "useOn", at = @At( value = "INVOKE",
            target = "Lnet/minecraft/world/item/context/UseOnContext;getClickedFace()Lnet/minecraft/core/Direction;"
    ), cancellable = true)
    private void hydrateDeadCoral(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        if (corals.isEmpty()) BlockRegistry.addCoral();
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player playerEntity = context.getPlayer();
        assert playerEntity!=null;
        ItemStack itemStack = context.getItemInHand();
        PotionContents potionContentsComponent = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        BlockState blockState = world.getBlockState(blockPos);
        if (corals.containsKey(blockState.getBlock()) && potionContentsComponent.is(Potions.WATER)) {
            world.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            playerEntity.setItemInHand(context.getHand(), ItemUtils.createFilledResult(itemStack, playerEntity, new ItemStack(Items.GLASS_BOTTLE)));
            playerEntity.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            if (!world.isClientSide()) {
                ServerLevel serverWorld = (ServerLevel)world;
                for (int i = 0; i < 5; i++) {
                    serverWorld.sendParticles(
                            ParticleTypes.SPLASH,
                            blockPos.getX() + world.getRandom().nextDouble(),
                            blockPos.getY() + 1,
                            blockPos.getZ() + world.getRandom().nextDouble(),
                            1,
                            0.0,
                            0.0,
                            0.0,
                            1.0
                    );
                }
            }

            world.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            if (blockState.getProperties().contains(BlockStateProperties.WATERLOGGED))
                if (blockState.getProperties().contains(HorizontalDirectionalBlock.FACING))
                    world.setBlockAndUpdate(blockPos, corals.get(blockState.getBlock()).defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)).setValue(HorizontalDirectionalBlock.FACING, blockState.getValue(HorizontalDirectionalBlock.FACING)));
                else world.setBlockAndUpdate(blockPos, corals.get(blockState.getBlock()).defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)));
            else world.setBlockAndUpdate(blockPos, corals.get(blockState.getBlock()).defaultBlockState());
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
