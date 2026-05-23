package net.greenjab.fixedminecraft.mixin.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @Shadow
    private int selected;

    @Shadow
    public abstract ItemStack getItem(int slot);

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Inject(method = "addResource(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    private void addItemsToBundle(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        if (tryBundle(getItem(selected), itemStack)) {
            cir.setReturnValue(0);
            return;
        }
        if (tryBundle(getItem(40), itemStack)) {
            cir.setReturnValue(0);
            return;
        }

        for (ItemStack item : items) {
            if (tryBundle(item, itemStack)) {
                cir.setReturnValue(0);
                return;
            }
        }
    }

    @Unique
    private boolean tryBundle(ItemStack bundle, ItemStack item) {
        if ( !bundle.isEmpty() && bundle.getComponents().has(DataComponents.BUNDLE_CONTENTS)){
            BundleContents bundleComponent = bundle.get(DataComponents.BUNDLE_CONTENTS);
            assert bundleComponent!=null;
            for (int i = 0; i < bundleComponent.size();i++) {
                ItemStack bundleStack = bundleComponent.items().get(i).create();
                BundleContents.Mutable builder = new BundleContents.Mutable(bundleComponent);
                if (ItemStack.isSameItemSameComponents(bundleStack, item)) {
                    builder.tryInsert(item);
                    bundle.set(DataComponents.BUNDLE_CONTENTS, builder.toImmutable());
                    if (item.isEmpty()) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

}
