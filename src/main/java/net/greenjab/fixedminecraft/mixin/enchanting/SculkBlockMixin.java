package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SculkBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SculkBlock.class)
public abstract class SculkBlockMixin extends DropExperienceBlock {

    public SculkBlockMixin(IntProvider xpRange, Properties properties) {
        super(xpRange, properties);
    }

    @Override
    protected void spawnAfterBreak(final @NonNull BlockState state, final @NonNull ServerLevel level, final @NonNull BlockPos pos, final @NonNull ItemStack tool, final boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, tool, true);
    }
}
