package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Makes leaves collision shape work for controlled entities like scaffolding.
 */
@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block {
    public LeavesBlockMixin(Properties settings) {
        super(settings);
    }

    // Allow the vehicle to both move through and walk on top of leaves
    @Override
    public @NonNull VoxelShape getCollisionShape(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            // If completely above the leaf block, treat as solid to allow standing on
            if (context.isAbove(Shapes.block(), pos, true) && !context.isDescending()) return super.getCollisionShape(state, world, pos, context);
                // If not, treat as empty
            else if (entityContext.getEntity() != null && (entityContext.getEntity().hasControllingPassenger()) || (entityContext.getEntity() instanceof Mob mobEntity && mobEntity.isLeashed())) return Shapes.empty();
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    // Prevent the camera from getting stuck on leaves
    @Override
    public @NonNull VoxelShape getVisualShape(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            // If camera-owning entity (player) is controlling a vehicle, treat as empty
            if (entityContext.getEntity() != null && entityContext.getEntity().getControlledVehicle() != null) return Shapes.empty();
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    // Slow down the vehicle when moving though leaves

    @Override
    protected void entityInside(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, Entity entity, @NonNull InsideBlockEffectApplier handler, boolean bl) {
        if (entity.hasControllingPassenger()) entity.makeStuckInBlock(state, new Vec3(0.85, 1, 0.85));
    }
}
