package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "checkBelowWorld", at = @At(
            value = "HEAD"
    ), cancellable = true)
    private void tridentReturnsFromVoid(CallbackInfo ci) {
        Entity E = (Entity) (Object)this;
        if (E instanceof ThrownTrident TE) {
            if (TE.getY() < (double)(TE.level().getMinY() - 48)) {
                int i = TE.level() instanceof ServerLevel serverWorld
                        ? (byte) Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverWorld, TE.getPickupItemStackOrigin(), E), 0, 127)
                        : 0;
                if (i>0) {
                    TE.addTag("void");
                    TE.setDeltaMovement(0, 0, 0);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "isInRain", at = @At(value = "HEAD"),cancellable = true)
    private void wetLingeringEffect(CallbackInfoReturnable<Boolean> cir) {
        Entity E = (Entity) (Object) this;
        for (AreaEffectCloud effectCloud : E.level().getEntitiesOfClass(AreaEffectCloud.class, E.getBoundingBox())) {
            if (effectCloud.getParticle().getType() == ParticleTypes.SPLASH) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "thunderHit", at = @At("HEAD"))
    private void healCopperGear(ServerLevel level, LightningBolt lightningBolt, CallbackInfo ci) {
        Entity E = (Entity) (Object)this;
        if (E instanceof Player PE) {
            Inventory inv = PE.getInventory();
            for (ItemStack itemStack : FixedMinecraft.getArmor(PE)) {
                if (itemStack.isValidRepairItem(Items.COPPER_INGOT.getDefaultInstance()))
                    itemStack.setDamageValue(0);
            }
            for (ItemStack itemStack : PE.inventoryMenu.getCraftSlots()) {
                if (itemStack.isValidRepairItem(Items.COPPER_INGOT.getDefaultInstance()))
                    itemStack.setDamageValue(0);
            }
            for (ItemStack itemStack : inv.getNonEquipmentItems()) {
                if (itemStack.isValidRepairItem(Items.COPPER_INGOT.getDefaultInstance()))
                    itemStack.setDamageValue(0);
            }
        }
    }
}
