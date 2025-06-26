package net.greenjab.fixedminecraft.mixin.inventory;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow
    private int selectedSlot;

    @Shadow
    public abstract ItemStack getStack(int slot);

    @Shadow
    @Final
    private DefaultedList<ItemStack> main;

    @Inject(method = "addStack(Lnet/minecraft/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    private void addItemsToBundle(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (tryBundle(getStack(selectedSlot), stack)) {
            cir.setReturnValue(0);
            return;
        }
        if (tryBundle(getStack(40), stack)) {
            cir.setReturnValue(0);
            return;
        }

        for (ItemStack itemStack : main) {
            if (tryBundle(itemStack, stack)) {
                cir.setReturnValue(0);
                return;
            }
        }
    }

    @Unique
    private boolean tryBundle(ItemStack bundle, ItemStack item) {
        if ( !bundle.isEmpty() && bundle.getComponents().contains(DataComponentTypes.BUNDLE_CONTENTS)){
            BundleContentsComponent bundleComponent = bundle.get(DataComponentTypes.BUNDLE_CONTENTS);
            for (int i = 0; i < bundleComponent.size();i++) {
                ItemStack bundleStack = bundleComponent.get(i);
                BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleComponent);
                if (ItemStack.areItemsAndComponentsEqual(bundleStack, item)) {
                    builder.add(item);
                    bundle.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
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
