package net.greenjab.fixedminecraft.mixin.enchanting;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Shadow
    private @Nullable IntOpenHashSet piercingIgnoreEntityIds;

    @Shadow
    public abstract ItemStack getPickupItemStackOrigin();

    @Inject(method = "setInGround", at = @At("HEAD"))
    private void removeEffectsIfPiecing(boolean inGround, CallbackInfo ci) {
        if (this.piercingIgnoreEntityIds==null) return;
        AbstractArrow PPE = (AbstractArrow)(Object)this;
        if (!this.piercingIgnoreEntityIds.isEmpty()) PPE.addTag("pierced");
    }

    @Redirect(method = "tryPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;getPickupItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack removeEffectsIfPiecing(AbstractArrow PPE) {
        if (!this.getPickupItemStackOrigin().is(Items.TIPPED_ARROW)) return PPE.getPickupItemStackOrigin().copy();
        if (PPE.entityTags().contains("pierced")) return Items.ARROW.getDefaultInstance();
        return PPE.getPickupItemStackOrigin().copy();
    }

}
