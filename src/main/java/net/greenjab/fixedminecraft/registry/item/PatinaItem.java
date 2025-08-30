package net.greenjab.fixedminecraft.registry.item;

import com.google.common.collect.BiMap;
import net.greenjab.fixedminecraft.registry.other.BrickEntity;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class PatinaItem extends Item {

    public PatinaItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Optional<ActionResult> ac = getOxidizedState(blockState).map(/* method_34719 */ blockState2 -> {
            PlayerEntity playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayerEntity, blockPos, itemStack);
            }

            itemStack.decrement(1);
            world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL_AND_REDRAW);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, blockState2));
            world.syncWorldEvent(playerEntity, WorldEvents.BLOCK_SCRAPED, blockPos, 0);

            return ActionResult.SUCCESS;
        });
        return ac.orElse(ActionResult.PASS);
    }

    public static Optional<BlockState> getOxidizedState(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
    }
}
