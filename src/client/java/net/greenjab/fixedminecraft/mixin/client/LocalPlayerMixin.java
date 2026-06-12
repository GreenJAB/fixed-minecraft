package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;tryToStartFallFlying()Z"))
    private boolean failRealTest(LocalPlayer instance) {
        return false;
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isFallFlying()Z"))
    private void addMyTest(CallbackInfo ci) {
        LocalPlayer CPE = (LocalPlayer)(Object)this;
        if (CPE.input.keyPresses.jump()) {
            if (!CPE.onClimbable() && !CPE.onGround() && !CPE.isPassenger() && !CPE.hasEffect(MobEffects.LEVITATION) &&
                (CPE.level().getDifficulty().getId()>1?!CPE.isInWaterOrRain():!CPE.isInWater()) &&
                !CPE.isInLava() &&
                CustomData.getData(CPE, "airTime") > 15) {
                if (CPE.tryToStartFallFlying()) {
                    CPE.connection.send(new ServerboundPlayerCommandPacket(CPE, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                }
            }
        }
    }
    @Inject(method = "vehicleCanSprint", at = @At("HEAD"), cancellable = true)
    private void horsesCanSprint(Entity vehicle, CallbackInfoReturnable<Boolean> cir){
        if (vehicle instanceof AbstractHorse) {
            cir.setReturnValue(true);
        }
    }

    @ModifyExpressionValue(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"))
    private static HitResult.Type dontModifyMaxDist(HitResult.Type original) {
        return HitResult.Type.MISS;
    }

    @ModifyExpressionValue(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D", ordinal = 1))
    private static double getEntityThroughGrass(double original, @Local(argsOnly = true) Entity cameraEntity,
                                                @Local(argsOnly = true, ordinal = 0) double blockInteractionRange,
                                                @Local(argsOnly = true, ordinal = 1) double entityInteractionRange,
                                                @Local(argsOnly = true) float partialTicks,
                                                @Local HitResult blockHitResult, @Local EntityHitResult entityHitResult) {
        double maxDistance = Math.max(blockInteractionRange, entityInteractionRange);
        Vec3 from = cameraEntity.getEyePosition(partialTicks);
        HitResult NewblockHitResult = pick(cameraEntity, maxDistance, partialTicks);

        if (blockHitResult.getLocation()!=NewblockHitResult.getLocation()) {
            double dist = entityHitResult.getLocation().distanceToSqr(from);
           if (dist < NewblockHitResult.getLocation().distanceToSqr(from) && dist < entityInteractionRange*entityInteractionRange) {
                return 0;
            }
        }
        return original;
    }

    @Unique
    private static HitResult pick(Entity cameraEntity, final double range, final float a) {
        Vec3 from = cameraEntity.getEyePosition(a);
        Vec3 viewVector = cameraEntity.getViewVector(a);
        Vec3 to = from.add(viewVector.x * range, viewVector.y * range, viewVector.z * range);
        return cameraEntity.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cameraEntity));
    }

    @ModifyVariable(method = "pick", at = @At(value = "HEAD"), ordinal = 1, argsOnly = true)
    private static double dist(double entityInteractionRange){
        assert Minecraft.getInstance().player != null;
        ItemStack weapon = Minecraft.getInstance().player.getMainHandItem();
        if (weapon.is(ItemTags.AXES)) entityInteractionRange -= 0.5;
        if (weapon.is(ItemTags.PICKAXES)) entityInteractionRange -= 0.5;
        if (weapon.is(ItemTags.SHOVELS)) entityInteractionRange -= 0.5;
        if (weapon.is(ItemTags.SWORDS)) entityInteractionRange += 0.0;
        if (weapon.is(ItemTags.TRIDENT_ENCHANTABLE)) entityInteractionRange += 0.5;
        if (weapon.is(ItemTags.HOES)) entityInteractionRange += 0.5;
        return entityInteractionRange;
    }
}
