package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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

    @Shadow private int selected;
    @Shadow public abstract ItemStack getItem(int slot);
    @Shadow @Final private NonNullList<ItemStack> items;
    @Shadow @Final public Player player;

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
        if (!bundle.isEmpty() && bundle.getComponents().has(DataComponents.BUNDLE_CONTENTS)){
            BundleContents bundleComponent = bundle.get(DataComponents.BUNDLE_CONTENTS);
            assert bundleComponent!=null;
            for (int i = 0; i < bundleComponent.size();i++) {
                ItemStack bundleStack = bundleComponent.items().get(i).create();
                BundleContents.Mutable builder = new BundleContents.Mutable(bundleComponent);
                if (ItemStack.isSameItemSameComponents(bundleStack, item)) {
                    builder.tryInsert(item);
                    bundle.set(DataComponents.BUNDLE_CONTENTS, builder.toImmutable());
                    if (item.isEmpty()) return true;
                    break;
                }
            }
        }
        return false;
    }

    @WrapOperation(method = "dropAll", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean noDropSpecialItems(ItemStack instance, Operation<Boolean> original) {
        if (this.player.level() instanceof ServerLevel level && !level.getGameRules().get(GameRuleRegistry.PARTIAL_KEEP_INVENTORY)) return original.call(instance);
        if (instance.is(ModTags.PARTIAL_KEEP_INVENTORY)) return true;
        return original.call(instance);
    }
}
