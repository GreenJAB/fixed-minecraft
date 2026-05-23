package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    @Final
    public InventoryMenu inventoryMenu;

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readCraftingGrid(ValueInput input, CallbackInfo ci) {
        CraftingContainer craftingGrid = inventoryMenu.getCraftSlots();
        NonNullList<ItemStack> stacks = ((CraftingInventoryAccessor) craftingGrid).getItems();
        ValueInput.TypedInputList<ItemStackWithSlot> items = input.listOrEmpty("CraftingItems", ItemStackWithSlot.CODEC);
        stacks.clear();
        for (ItemStackWithSlot stackWithSlot : items) {
            if (stackWithSlot.isValidInContainer(4)) {
                stacks.set(stackWithSlot.slot(), stackWithSlot.stack());
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writeCraftingGrid(ValueOutput output, CallbackInfo ci) {
        CraftingContainer craftingGrid = inventoryMenu.getCraftSlots();

        ValueOutput.TypedOutputList<ItemStackWithSlot> list = output.list("CraftingItems", ItemStackWithSlot.CODEC);
        for (int i = 0; i < craftingGrid.getContainerSize(); i++) {
            ItemStack itemStack = craftingGrid.getItem(i);
            if (!itemStack.isEmpty()) {
                list.add(new ItemStackWithSlot(i, itemStack));
            }
        }
    }

    @Inject(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"))
    private void dropCraftingGridItems(ServerLevel level, CallbackInfo ci) {
        if (!level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            Player PE = (Player) (Object) this;
            for (ItemStack itemStack : PE.inventoryMenu.getCraftSlots().getItems()) {
                PE.drop(itemStack, false);
            }
        }
    }

    @Inject(method = "getProjectile", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"),
            cancellable = true
    )
    private void arrowsInBundle(ItemStack heldWeapon, CallbackInfoReturnable<ItemStack> cir,
                                @Local(ordinal = 2) ItemStack bundle,
                                @Local Predicate<ItemStack> supportedProjectiles) {
        if ( !bundle.isEmpty() && bundle.getComponents().has(DataComponents.BUNDLE_CONTENTS)){
            BundleContents bundleComponent = bundle.get(DataComponents.BUNDLE_CONTENTS);
            assert bundleComponent!=null;
            for (int i = 0; i < bundleComponent.size();i++) {
                ItemStack bundleStack = bundleComponent.items().get(i).create();
                if (supportedProjectiles.test(bundleStack)) {
                    DataComponentPatch components = DataComponentPatch.builder().set(DataComponents.REPAIR_COST, 4).build();
                    bundleStack.applyComponents(components);
                    cir.setReturnValue(bundleStack);
                    return;
                }
            }
        }
    }
}
