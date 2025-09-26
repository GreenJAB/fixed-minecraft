package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void getEntityThroughGrass(float tickDelta, CallbackInfo ci) {
        Entity entity = this.client.getCameraEntity();
        assert client.player != null;
        double d = client.player.getBlockInteractionRange();
        assert entity != null;
        client.crosshairTarget = entity.raycast(d, tickDelta, false);
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        assert client.interactionManager != null;
        boolean bl = client.interactionManager.getCurrentGameMode().isCreative();
        d = bl ? 6.0 : d;
        boolean bl2 = !bl;
        double e = client.crosshairTarget != null ? client.crosshairTarget.getPos().squaredDistanceTo(vec3d) : d * d;
        Vec3d vec3d2 = entity.getRotationVec(1.0F);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float f = 1.0F;
        Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(f, f, f);
        Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit();

        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, predicate, e);

        Entity vehicle = client.player.getVehicle();
        if (entityHitResult != null && (!entityHitResult.getEntity().hasVehicle() || entityHitResult.getEntity().getVehicle() != vehicle)) {
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if (bl2 && g > dist()) {
                client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), BlockPos.ofFloored(vec3d4));
                client.targetedEntity = null;
            } else if (g < e || client.crosshairTarget == null) {
                client.crosshairTarget = entityHitResult;
                client.targetedEntity = entityHitResult.getEntity();
            }
        }

        if (client.crosshairTarget instanceof BlockHitResult hit && client.targetedEntity == null) {
            assert client.world != null;
            if (client.world.getBlockState(hit.getBlockPos()).getCollisionShape(client.world, hit.getBlockPos()).isEmpty()) {
                EntityHitResult entityHitResult2 = ProjectileUtil.getEntityCollision(entity.getEntityWorld(), entity, vec3d, vec3d3, box, predicate, 0.0f);
                if (entityHitResult2 != null && entityHitResult2.getEntity() != vehicle) {
                    HitResult hitResult = client.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
                    if (bl2 && vec3d.squaredDistanceTo(entityHitResult2.getPos()) < dist()) {
                        if (entityHitResult2.getPos().squaredDistanceTo(vec3d) < hitResult.getPos().squaredDistanceTo(vec3d)) {
                            client.crosshairTarget = entityHitResult2;
                            client.targetedEntity = entityHitResult2.getEntity();
                        }
                    } else if (bl) {
                        if (entityHitResult2.getPos().squaredDistanceTo(vec3d) < hitResult.getPos().squaredDistanceTo(vec3d)) {
                            client.crosshairTarget = entityHitResult2;
                            client.targetedEntity = entityHitResult2.getEntity();
                        }
                    }
                }
            }
        }
        if (client.player.getHungerManager().getSaturationLevel()==0) {
            client.crosshairTarget = entity.raycast(d, tickDelta, false);
            client.targetedEntity = null;
        }
    }

    @Unique
    private double dist(){
        double d = 3;
        assert client.player != null;
        ItemStack weapon = client.player.getMainHandStack();
        if (weapon.isIn(ItemTags.AXES)) d = 2.5;
        if (weapon.isIn(ItemTags.PICKAXES)) d = 2.5;
        if (weapon.isIn(ItemTags.SHOVELS)) d = 2.5;
        if (weapon.isIn(ItemTags.SWORDS)) d = 3;
        if (weapon.isOf(Items.TRIDENT)) d = 3.5;
        if (weapon.isIn(ItemTags.HOES)) d = 3.5;
        if (client.player.isCreative())d+=3;
        return d*d;
    }
}
