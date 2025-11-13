package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.greenjab.fixedminecraft.FixedMinecraft.corals;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {

        @Inject(method = "useOnBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemUsageContext;getSide()Lnet/minecraft/util/math/Direction;"
    ), cancellable = true)
    private void hydrateDeadCoral(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        if (corals.isEmpty()) BlockRegistry.addCoral();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();
        assert playerEntity!=null;
        ItemStack itemStack = context.getStack();
        PotionContentsComponent potionContentsComponent = itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
        BlockState blockState = world.getBlockState(blockPos);
        if (corals.containsKey(blockState.getBlock()) && potionContentsComponent.matches(Potions.WATER)) {
            world.playSound(null, blockPos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            playerEntity.setStackInHand(context.getHand(), ItemUsage.exchangeStack(itemStack, playerEntity, new ItemStack(Items.GLASS_BOTTLE)));
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
            if (!world.isClient()) {
                ServerWorld serverWorld = (ServerWorld)world;
                for (int i = 0; i < 5; i++) {
                    serverWorld.spawnParticles(
                            ParticleTypes.SPLASH,
                            blockPos.getX() + world.random.nextDouble(),
                            blockPos.getY() + 1,
                            blockPos.getZ() + world.random.nextDouble(),
                            1,
                            0.0,
                            0.0,
                            0.0,
                            1.0
                    );
                }
            }

            world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            if (blockState.getProperties().contains(Properties.WATERLOGGED))
                if (blockState.getProperties().contains(HorizontalFacingBlock.FACING))
                    world.setBlockState(blockPos, corals.get(blockState.getBlock()).getDefaultState().with(Properties.WATERLOGGED, blockState.get(Properties.WATERLOGGED)).with(HorizontalFacingBlock.FACING, blockState.get(HorizontalFacingBlock.FACING)));
                else world.setBlockState(blockPos, corals.get(blockState.getBlock()).getDefaultState().with(Properties.WATERLOGGED, blockState.get(Properties.WATERLOGGED)));
            else world.setBlockState(blockPos, corals.get(blockState.getBlock()).getDefaultState());
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
