package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Makes leaves collision shape work for controlled entities like scaffolding.
 */
@Mixin(LeavesBlock.class)
@SuppressWarnings("deprecation")
public abstract class LeavesBlockMixin extends Block {
    public LeavesBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(context instanceof EntityShapeContext entityContext))
            return super.getCollisionShape(state, world, pos, context);
        Entity entity = entityContext.getEntity();
        if (entity == null || !entity.hasControllingPassenger())
            return super.getCollisionShape(state, world, pos, context);
        if (context.isAbove(VoxelShapes.fullCube(), pos, true) && !context.isDescending())
            return super.getCollisionShape(state, world, pos, context);
        return VoxelShapes.empty();

    }
}
