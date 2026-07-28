package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Shadow private @Nullable IntOpenHashSet piercingIgnoreEntityIds;

    @Inject(method = "setInGround", at = @At("HEAD"))
    private void removeEffectsIfPiecing(boolean inGround, CallbackInfo ci) {
        if (this.piercingIgnoreEntityIds==null) return;
        AbstractArrow PPE = (AbstractArrow)(Object)this;
        if (!this.piercingIgnoreEntityIds.isEmpty()) PPE.addTag("pierced");
    }

    @WrapOperation(method = "tryPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;getPickupItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack removeEffectsIfPiecing(AbstractArrow instance, Operation<ItemStack> original) {
        if (instance.entityTags().contains("pierced")) return Items.ARROW.getDefaultInstance();
        return original.call(instance);
    }
}
