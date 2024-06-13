package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LeavesBlock.class)
public abstract class VehicleLeafCollisionMixin extends Block {
    // 2-pixel tall plane at the top of the block (might not be necessary, just grabbed from scaffolding)
    @Unique
    private static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);

    public LeafBlockCollision(Settings settings) { super(settings); }

    // Allow the vehicle to both move through and walk on top of leaves
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext entityContext) {
            // If completely above the leaf block, treat as solid to allow standing on
            if (context.isAbove(VoxelShapes.fullCube(), pos, true) && !context.isDescending()) return COLLISION_SHAPE;
            // If not, treat as empty
            else if (entityContext.getEntity() != null && entityContext.getEntity().hasControllingPassenger()) return VoxelShapes.empty();
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    // Prevent the camera from getting stuck on leaves
    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext entityContext) {
            // If camera-owning entity (player) is controlling a vehicle, treat as empty
            if (entityContext.getEntity() != null && entityContext.getEntity().getControllingVehicle() != null) return VoxelShapes.empty();
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    // Slow down the vehicle when moving though leaves
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity.hasControllingPassenger()) entity.slowMovement(state, new Vec3d(0.85, 1, 0.85));
    }
}
