package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    MinecraftClient client;


    @Inject(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getReachDistance()F"), cancellable = true)
    private void i(float tickDelta, CallbackInfo ci, @Local(ordinal = 0) Entity entity) {
        double d = this.client.interactionManager.getReachDistance();
        this.client.crosshairTarget = entity.raycast(d, tickDelta, false);
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        boolean bl = this.client.interactionManager.hasExtendedReach();
        d = bl ? 6.0 : d;
        boolean bl2 = !bl;
        double e = this.client.crosshairTarget != null ? this.client.crosshairTarget.getPos().squaredDistanceTo(vec3d) : d * d;
        Vec3d vec3d2 = entity.getRotationVec(1.0F);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float f = 1.0F;
        Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit();
        EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(entity.getWorld(), entity, vec3d, vec3d3, box, predicate, 0.15f);
        if (entityHitResult == null) {
            entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, predicate, e);
        }
        if (entityHitResult != null) {
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if (bl2 && g > dist()) {
                this.client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), BlockPos.ofFloored(vec3d4));
            } else if (g < e+0.75 || this.client.crosshairTarget == null) {
                this.client.crosshairTarget = entityHitResult;
                this.client.targetedEntity = entityHitResult.getEntity();
            } else {
                if (client.crosshairTarget instanceof BlockHitResult hit) {
                    if(this.client.world.getBlockState(hit.getBlockPos()).getCollisionShape(this.client.world, hit.getBlockPos()).isEmpty()) {
                        //currently can hit through solid blocks if standing in grass
                        this.client.crosshairTarget = entityHitResult;
                        this.client.targetedEntity = entityHitResult.getEntity();
                    }
                }
            }
        }
        this.client.getProfiler().pop();
        ci.cancel();
    }

    @Unique
    private double dist(){
        double d =  2.5;
        ItemStack weapon = this.client.player.getMainHandStack();
        if (weapon.isIn(ItemTags.SWORDS)) d = 3;
        if (weapon.isIn(ItemTags.AXES)) d = 2.5;
        if (weapon.isOf(Items.TRIDENT)) d = 3.5;
        if (weapon.isIn(ItemTags.HOES)) d = 3.5;
        if (weapon.isIn(ItemTags.PICKAXES)) d = 2.5;
        if (weapon.isIn(ItemTags.SHOVELS)) d = 2.5;
        if (this.client.player.isCreative())d+=3;
        //System.out.println(d);
        return d*d;
    }

    /*@ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double LongerEntityReach(double constant) {
        double d =  2.5;
        ItemStack weapon = this.client.player.getMainHandStack();
        if (weapon.isIn(ItemTags.SWORDS)) d = 3;
        if (weapon.isIn(ItemTags.AXES)) d = 2.5;
        if (weapon.isOf(Items.TRIDENT)) d = 3.5;
        if (weapon.isIn(ItemTags.HOES)) d = 3.5;
        if (weapon.isIn(ItemTags.PICKAXES)) d = 2.5;
        if (weapon.isIn(ItemTags.SHOVELS)) d = 2.5;
        if (this.client.player.isCreative())d+=3;
        System.out.println(d);
        return d*d;
    }

    @ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE", ordinal = 1))
    private double i(double maxDistance){

        return 1000;
    }
    /*@Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    private @Nullable EntityHitResult i(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double maxDistance){
        /*if (original!=null) {
            System.out.println(original);
        }
        return original;*
        EntityHitResult hit =
        //return
                ProjectileUtil.getEntityCollision(entity.getWorld(), entity, min, max, box, predicate, 0.1f);
        System.out.println(hit);
        return hit;
    }*/
    /*@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"))
    private EntityHitResult i(EntityHitResult value, @Local(ordinal = 0) Entity entity, @Local(ordinal = 0) Vec3d min, @Local(ordinal = 2) Vec3d max, @Local Box box) {
        Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit();
        EntityHitResult hit = ProjectileUtil.getEntityCollision(entity.getWorld(), entity, min, max, box, predicate, 0.1f);
        if (hit == null) {
            hit = value;
        }
        System.out.println(hit);
        return hit;
    }
    /*@Inject(method = "updateTargetedEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;client:Lnet/minecraft/client/MinecraftClient;", ordinal = 12))
    private void hitThroughGrass(float tickDelta, CallbackInfo ci, @Local(ordinal = 0) Entity entity, @Local(ordinal = 0) Vec3d min, @Local(ordinal = 2) Vec3d max, @Local Box box) {

    }*/
        /*if (original!=null) {
            System.out.println(original);
        }
        return original;*
        EntityHitResult hit =
        //return
                ProjectileUtil.getEntityCollision(entity.getWorld(), entity, min, max, box, predicate, 0.1f);
        System.out.println(hit);
        return hit;
    }*/
}
