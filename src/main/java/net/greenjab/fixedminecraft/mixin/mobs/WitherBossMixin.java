package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
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

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {

    @Redirect(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;heal(F)V", ordinal = 1))
    private void dontHealPastHalfHealth(WitherBoss instance, float v){
        instance.heal(1.0f);
        if (instance.entityTags().contains("phase2")) {
            if (instance.getHealth() > instance.getMaxHealth() / 2.0F) {
                instance.setHealth(instance.getMaxHealth() / 2.0F);
            }
        }
   }

    @Inject(method = "customServerAiStep", at = @At(value = "HEAD"))
    private void noclipBelowHalfHealth(CallbackInfo ci){
        WitherBoss WE = (WitherBoss) (Object)this;
        if (WE.isPowered() && WE.getInvulnerableTicks() <=0) {
            WE.noPhysics=true;
            if (!WE.entityTags().contains("phase2")) {
                WE.addTag("phase2");
                WE.level().explode(
                        WE, WE.getX(), WE.getY(), WE.getZ(), 5, Level.ExplosionInteraction.MOB
                );
                for (int i = 0;i<3;i++) {
                    WitherSkeleton WSE = EntityType.WITHER_SKELETON.create(WE.level().getChunkAt(WE.blockPosition()).getLevel(), EntitySpawnReason.MOB_SUMMONED);
                    assert WSE != null;
                    WSE.snapTo(WE.getX(), WE.getY(), WE.getZ(), 0.0F, 0.0F);
                    WSE.setDeltaMovement(Math.cos(i*120*Math.PI/180.0), 0, Math.sin(i*120*Math.PI/180.0));
                    WSE.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                    WSE.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.STONE_BUTTON));
                    WSE.setDropChance(EquipmentSlot.HEAD, 0);
                    WE.level().addFreshEntity(WSE);
                }
            }

        }
    }

    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private Vec3 floatUpInBlocks(Vec3 vec3d) {
        WitherBoss WE = (WitherBoss) (Object)this;
        Level world = WE.level();
        BlockPos blockpos = WE.blockPosition();
        ChunkPos chunk = world.getChunkAt(blockpos).getPos();
        BlockGetter blockView = world.getChunkForCollisions(chunk.x(), chunk.z());
        if (world.getBlockState(blockpos).isRedstoneConductor(blockView, blockpos) && !world.getBlockState(blockpos.above()).is(Blocks.BEDROCK)) {
            return vec3d.add(0, 0.05 - vec3d.y * 0.6F, 0);
        } else {
            return vec3d;
        }
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "STORE"), ordinal = 1)
    private Vec3 strafePlayer(Vec3 delta, @Local Entity entity) {
        WitherBoss WE = (WitherBoss) (Object)this;
        double r = 6;
        double dx = WE.getX()- entity.getX();
        double dz = WE.getZ() - entity.getZ();
        double dh = Math.sqrt(dx*dx+dz*dz);
        double p = Math.max(0, 2*r-dh);
        double pm = Math.max(0, r-Math.abs(r-p));
        double nx =((-dx*(r-p))/r)+((dz*pm)/dh);
        double nz =((-dz*(r-p))/r)-((dx*pm)/dh);
        return new Vec3(nx, 0, nz).normalize();
    }

    @ModifyConstant(method = "aiStep", constant = @Constant(doubleValue = 9.0))
    private double dontStop(double v){
        return 0;
    }
    @ModifyConstant(method = "aiStep", constant = @Constant(doubleValue = 0.3, ordinal = 1))
    private double moveSlower1(double v){
        return v*(((WitherBoss) (Object)this).isPowered()?0.9:0.5);
    }
    @ModifyConstant(method = "aiStep", constant = @Constant(doubleValue = 0.3, ordinal = 2))
    private double moveSlower2(double v){
        return v*(((WitherBoss) (Object)this).isPowered()?0.9:0.5);
    }

    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;setYRot(F)V"), index = 0)
    private float facePlayer(float v){
        WitherBoss WE = (WitherBoss) (Object)this;
        if (!WE.level().isClientSide()){
            if (WE.getAlternativeTarget(0) > 0) {
                Entity entity = WE.level().getEntity(WE.getAlternativeTarget(0));
                if (entity != null) {
                    double dx = entity.getX()-WE.getX();
                    double dz = entity.getZ()-WE.getZ();
                    return (float) Mth.atan2(dz, dx) * (180.0F / (float)Math.PI) - 90.0F;
                }
            } else {
                return v;
            }
        }
         return WE.yBodyRot;
    }

    @Inject(method = "hurtServer", at = @At(value = "HEAD"))
    private void addGlowingEffect(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir){
        WitherBoss WE = (WitherBoss) (Object)this;
        if (source.getDirectEntity() instanceof SpectralArrow) {
            WE.forceAddEffect(new MobEffectInstance(MobEffects.GLOWING, 600), source.getEntity());
        }
    }
}
