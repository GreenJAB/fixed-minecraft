package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {
    @Inject(method = "onCollision", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/projectile/thrown/PotionEntity;applyWater()V"
    ))
    private void waterAreaEffect(HitResult hitResult, CallbackInfo ci) {
        PotionEntity PE = (PotionEntity) (Object)this;
        if (PE.getStack().isOf(Items.LINGERING_POTION)) {
            this.applyLingeringPotion(PE.getStack());
        }
    }




    @Unique
    private void applyLingeringPotion(ItemStack stack) {
        PotionEntity PE = (PotionEntity) (Object)this;
        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(PE.getWorld(), PE.getX(), PE.getY(), PE.getZ());
        Entity entity = PE.getOwner();
        if (entity instanceof LivingEntity) {
            areaEffectCloudEntity.setOwner((LivingEntity)entity);
        }

        areaEffectCloudEntity.setRadius(3.0F);
        areaEffectCloudEntity.setRadiusOnUse(-0.5F);
        areaEffectCloudEntity.setWaitTime(10);
        areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
        areaEffectCloudEntity.setPotion(Potions.WATER);
        areaEffectCloudEntity.addCommandTag("water");

        for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(stack)) {
            areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
        }

        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains("CustomPotionColor", NbtElement.NUMBER_TYPE)) {
            areaEffectCloudEntity.setColor(nbtCompound.getInt("CustomPotionColor"));
        }

        PE.getWorld().spawnEntity(areaEffectCloudEntity);
    }
}
