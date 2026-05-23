package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.registry.other.FletchingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class NewFletchingTableBlock extends Block {
    public static final MapCodec<NewFletchingTableBlock> CODEC = simpleCodec(NewFletchingTableBlock::new);
    private static final Component TITLE = Component.translatable("container.fletching");

    @Override
    public @NonNull MapCodec<NewFletchingTableBlock> codec() {
        return CODEC;
    }

    public NewFletchingTableBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level world, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (!world.isClientSide()) {
            player.openMenu(state.getMenuProvider(world, pos));
            player.awardStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos) {
        return new SimpleMenuProvider(
                 (syncId, inventory, _) -> new FletchingMenu(syncId, inventory, ContainerLevelAccess.create(world, pos)), TITLE
        );
    }
}
