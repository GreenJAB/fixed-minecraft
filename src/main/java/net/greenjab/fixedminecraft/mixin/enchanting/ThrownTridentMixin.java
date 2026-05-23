package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin {

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> ID_FOIL;

    @Shadow
    public abstract ItemStack getWeaponItem();

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V", ordinal = 0
    ))
    private void dispenserShoot(Level level, LivingEntity owner, ItemStack tridentItem, CallbackInfo ci) {
        if (owner instanceof Pig) {
            ThrownTrident TE = (ThrownTrident) (Object)this;
            TE.getEntityData().set(ID_FOIL, tridentItem.hasFoil());
            TE.pickup = AbstractArrow.Pickup.ALLOWED;
        }
    }

    @ModifyExpressionValue(method = "onHitEntity", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;modifyDamage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)F"
    ))
    private float impalingEffectsWetMobs(float original, @Local(ordinal = 0) Entity entity) {
        if (entity instanceof LivingEntity) {
            ItemEnchantments enchantments = this.getWeaponItem().getEnchantments();
            int i = 0;
            for (Holder<Enchantment> entry : enchantments.keySet()) {
                if (entry.unwrapKey().isPresent() && entry.unwrapKey().get().equals(Enchantments.IMPALING)) {
                    i = enchantments.getLevel(entry);
                }
            }
            return original +
                   (( entity.is(EntityTypeTags.AQUATIC) || entity.isInWaterOrRain()) ? i * 1.5F : 0.0F);
        }
        return original;
    }

    @ModifyExpressionValue(method = "tick", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;inGroundTime:I",
            opcode = Opcodes.GETFIELD
    ))
    private int returnVoidTrident(int original) {
        ThrownTrident TE = (ThrownTrident) (Object)this;
        if (TE.entityTags().contains("void")) return 5;
        return original;
    }
}
