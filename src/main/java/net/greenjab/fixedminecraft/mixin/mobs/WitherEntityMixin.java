package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherEntity.class)
public class WitherEntityMixin {

    @Redirect(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/WitherEntity;heal(F)V", ordinal = 1))
    private void dontHealPastHalfHealth(WitherEntity instance, float v){
        instance.heal(1.0f);
        if (instance.getCommandTags().contains("phase2")) {
            if (instance.getHealth() > instance.getMaxHealth() / 2.0F) {
                instance.setHealth(instance.getMaxHealth() / 2.0F);
            }
        }
   }

    @Inject(method = "mobTick", at = @At(value = "HEAD"))
    private void noclipBelowHalfHealth(CallbackInfo ci){
        WitherEntity WE = (WitherEntity) (Object)this;
        if (WE.shouldRenderOverlay() && WE.getInvulnerableTimer() <=0) {
            WE.noClip=true;
            if (!WE.getCommandTags().contains("phase2")) {
                WE.addCommandTag("phase2");
                WE.getWorld().createExplosion(
                        WE, WE.getX(), WE.getY(), WE.getZ(), 5, World.ExplosionSourceType.MOB
                );
                for (int i = 0;i<3;i++) {
                    WitherSkeletonEntity WSE = EntityType.WITHER_SKELETON.create(WE.getWorld().getWorldChunk(WE.getBlockPos()).getWorld(), SpawnReason.MOB_SUMMONED);
                    WSE.refreshPositionAndAngles(WE.getX(), WE.getY(), WE.getZ(), 0.0F, 0.0F);
                    WSE.setVelocity(Math.cos(i*120*Math.PI/180.0), 0, Math.sin(i*120*Math.PI/180.0));
                    WSE.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                    WSE.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.STONE_BUTTON));
                    WSE.setEquipmentDropChance(EquipmentSlot.HEAD, 0);
                    WE.getWorld().spawnEntity(WSE);
                }
            }

        }
    }

    @ModifyArg(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/WitherEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"), index = 0)
    private Vec3d floatUpInBlocks(Vec3d vec3d) {
        WitherEntity WE = (WitherEntity) (Object)this;
        if (WE.getWorld().getBlockState(WE.getBlockPos()).isSolid() && !WE.getWorld().getBlockState(WE.getBlockPos().up()).isOf(Blocks.BEDROCK)) {
            return vec3d.add(0, 0.05 - vec3d.y * 0.6F, 0);
        } else {
            return vec3d;
        }
    }

    @ModifyVariable(method = "tickMovement", at = @At(value = "STORE"), ordinal = 1)
    private Vec3d strafePlayer(Vec3d value, @Local Entity entity) {
        WitherEntity WE = (WitherEntity) (Object)this;
        double r = 6;
        double dx = WE.getX()- entity.getX();
        double dz = WE.getZ() - entity.getZ();
        double dh = Math.sqrt(dx*dx+dz*dz);
        double p = Math.max(0, 2*r-dh);
        double pm = Math.max(0, r-Math.abs(r-p));
        double nx =((-dx*(r-p))/r)+((dz*pm)/dh);
        double nz =((-dz*(r-p))/r)-((dx*pm)/dh);
        return new Vec3d(nx, 0, nz).normalize();
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 9.0))
    private double dontStop(double v){
        return 0;
    }
    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.3, ordinal = 1))
    private double moveSlower1(double v){
        return v*(((WitherEntity) (Object)this).shouldRenderOverlay()?0.9:0.5);
    }
    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.3, ordinal = 2))
    private double moveSlower2(double v){
        return v*(((WitherEntity) (Object)this).shouldRenderOverlay()?0.9:0.5);
    }

    @ModifyArg(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/WitherEntity;setYaw(F)V"), index = 0)
    private float facePlayer(float v){
        WitherEntity WE = (WitherEntity) (Object)this;
        if (!WE.getWorld().isClient ){
            if (WE.getTrackedEntityId(0) > 0) {
                Entity entity = WE.getWorld().getEntityById(WE.getTrackedEntityId(0));
                if (entity != null) {
                    double dx = entity.getX()-WE.getX();
                    double dz = entity.getZ()-WE.getZ();
                    return (float) MathHelper.atan2(dz, dx) * (180.0F / (float)Math.PI) - 90.0F;
                }
            } else {
                return v;
            }
        }
         return WE.bodyYaw;
    }

    @Inject(method = "damage", at = @At(value = "HEAD"))
    private void addGlowingEffect(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        WitherEntity WE = (WitherEntity) (Object)this;
        if (source.getSource() instanceof SpectralArrowEntity) {
            WE.setStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 600), source.getAttacker());
        }
    }
}
