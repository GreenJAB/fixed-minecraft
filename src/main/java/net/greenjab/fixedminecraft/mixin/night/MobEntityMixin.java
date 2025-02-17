package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "initialize", at=@At(value = "HEAD"))
    private void addNightTag(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData,
                             CallbackInfoReturnable<EntityData> cir){
        MobEntity LE = (MobEntity)(Object)this;
        if (LE instanceof HostileEntity HE) {
            if (world.getLightLevel(LightType.SKY, HE.getBlockPos())>10 && world.getAmbientDarkness() < 5) {
                HE.addCommandTag("Night");
            }
        }
    }

    /*@ModifyExpressionValue(method = "equipBodyArmor", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;BODY:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }

    @ModifyExpressionValue(method = "isWearingBodyArmor", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;BODY:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet2(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }

    @ModifyExpressionValue(method = "canUseSlot", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;BODY:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet3(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }*/

    @Inject(method = "canUseSlot", at = @At(value = "HEAD"), cancellable = true)
    private void muleArmourslot(EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir){
        MobEntity LE = (MobEntity) (Object)this;
        if (LE instanceof MuleEntity) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
