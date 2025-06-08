package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {

    @Shadow
    private @Nullable IntOpenHashSet piercedEntities;

    @Shadow
    public abstract ItemStack getItemStack();

    @Shadow
    protected boolean inGround;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE",
                                                     target = "Lnet/minecraft/util/math/Box;contains(Lnet/minecraft/util/math/Vec3d;)Z"
    ))
    private boolean removeEffectsIfPiecing(boolean original) {
        if (original) {
            if (this.piercedEntities == null) return original;
            PersistentProjectileEntity PPE = (PersistentProjectileEntity) (Object) this;
            if (!this.piercedEntities.isEmpty()) {
                PPE.addCommandTag("pierced");
            }
        }
        return original;
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"))
    private void removeEffectsIfPiecing2(BlockHitResult blockHitResult, CallbackInfo ci) {
        if (this.piercedEntities==null) return;
        PersistentProjectileEntity PPE = (PersistentProjectileEntity)(Object)this;
        if (!this.piercedEntities.isEmpty()) {
            PPE.addCommandTag("pierced");
        }
    }

    @Redirect(method = "tryPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;asItemStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack removeEffectsIfPiecing(PersistentProjectileEntity PPE) {
        if (!this.getItemStack().isOf(Items.TIPPED_ARROW)) return PPE.getItemStack().copy();
        if (PPE.getCommandTags().contains("pierced")) return Items.ARROW.getDefaultStack();
        return PPE.getItemStack().copy();
    }
}
