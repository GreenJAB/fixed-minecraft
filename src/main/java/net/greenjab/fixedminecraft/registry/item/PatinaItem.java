package net.greenjab.fixedminecraft.registry.item;

import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.NonNull;

public class PatinaItem extends Item {

    public PatinaItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockPos);
        Optional<InteractionResult> ac = getOxidizedState(blockState).map( blockState2 -> {
            Player playerEntity = context.getPlayer();
            ItemStack itemStack = context.getItemInHand();
            if (playerEntity instanceof ServerPlayer serverPlayerEntity) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayerEntity, blockPos, itemStack);
            }

            itemStack.shrink(1);
            world.setBlock(blockPos, blockState2, Block.UPDATE_ALL_IMMEDIATE);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(playerEntity, blockState2));
            world.levelEvent(playerEntity, LevelEvent.PARTICLES_SCRAPE, blockPos, 0);
            if (blockState.getBlock() instanceof ChestBlock && blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                BlockPos blockPos2 = ChestBlock.getConnectedBlockPos(blockPos, blockState);
                world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos2, GameEvent.Context.of(playerEntity, world.getBlockState(blockPos2)));
                world.levelEvent(playerEntity, LevelEvent.PARTICLES_SCRAPE, blockPos2, 0);
            }

            return InteractionResult.SUCCESS;
        });
        return ac.orElse(InteractionResult.PASS);
    }

    public static Optional<BlockState> getOxidizedState(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).map(block -> block.withPropertiesOf(state));
    }
}
