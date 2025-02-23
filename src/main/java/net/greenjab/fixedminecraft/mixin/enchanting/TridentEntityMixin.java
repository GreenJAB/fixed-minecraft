package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {

    @Shadow
    @Final
    private static TrackedData<Boolean> ENCHANTED;

    @Shadow
    public abstract ItemStack getWeaponStack();

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/data/DataTracker;set(Lnet/minecraft/entity/data/TrackedData;Ljava/lang/Object;)V", ordinal = 0
    ))
    private void dispenserShoot(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci) {
        if (owner instanceof PigEntity) {
            TridentEntity TE = (TridentEntity) (Object)this;
            TE.getDataTracker().set(ENCHANTED, stack.hasGlint());
            TE.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        }
    }

    @ModifyExpressionValue(method = "onEntityHit", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;F)F"
    ))
    private float impalingEffectsWetMobs(float original, @Local(ordinal = 0) Entity entity) {
        if (entity instanceof LivingEntity) {
            //int i = EnchantmentHelper.getLevel(Enchantments.IMPALING, PE.getMainHandStack());
            ItemEnchantmentsComponent enchantments = this.getWeaponStack().getEnchantments();
            int i = 0;
            for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                if (entry.getKey().get().equals(Enchantments.IMPALING)) {
                    i = enchantments.getLevel(entry);
                }
            }
            return original +
                   ((( entity).getType().isIn(EntityTypeTags.AQUATIC) || entity.isTouchingWaterOrRain()) ? i * 1.5F : 0.0F);
        }
        return original;
    }

    @ModifyExpressionValue(method = "tick", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/projectile/TridentEntity;inGroundTime:I"
    ))
    private int returnVoidTrident(int original) {
        TridentEntity TE = (TridentEntity) (Object)this;
        if (TE.getCommandTags().contains("void")) return 5;
        return original;
    }
}
