package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
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
 * Makes leaves collision shape work like scaffolding with some nuances.
 * <p>
 * Traversing leaves is possible by any entity, but a slowdown is applied depending on the entity size.
 * Bigger entities move faster, since they are stronger (in theory). Vertical movement is unaffected by it,
 * because it's leaves duh, you can't just fly in them.
 * <p>
 * Leaves are added to #climbable tag to accompany this change.
 * This is based on the assumption of leaves having small branches inside like real trees.
 */
@Mixin(LeavesBlock.class)
@SuppressWarnings("deprecation")
public abstract class LeavesBlockMixin extends Block {
    // TODO: Move this to the config when it's done
    @Unique
    private float minVol = 1F;

    @Unique
    private float maxVol = 3F;

    public LeavesBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return context.isAbove(VoxelShapes.fullCube(), pos, true) && !context.isDescending()
                ? state.getOutlineShape(world, pos)
                : VoxelShapes.empty();
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        float modifier = MathHelper.clamp(entity.getHeight() * entity.getWidth() * entity.getWidth(), minVol, maxVol) / maxVol;
        entity.slowMovement(state, new Vec3d(modifier, 0.5F, modifier));
    }
}
