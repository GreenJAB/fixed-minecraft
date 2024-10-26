package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Saves player crafting inventory across restarts.
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Shadow
    @Final
    public PlayerScreenHandler playerScreenHandler;

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readCraftingGrid(NbtCompound nbt, CallbackInfo ci) {
        RecipeInputInventory craftingGrid = playerScreenHandler.getCraftingInput();
        DefaultedList<ItemStack> stacks = ((CraftingInventoryAccessor) craftingGrid).getStacks();
        NbtList items = nbt.getList("CraftingItems", NbtElement.COMPOUND_TYPE);
        if (items == null) return;
        stacks.clear();
        for (int i = 0; i < items.size(); ++i) {
            NbtCompound nbtCompound = items.getCompound(i);
            int slot = nbtCompound.getByte("Slot") & 255;
            if (slot >= craftingGrid.size()) continue;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            stacks.set(slot, itemStack);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeCraftingGrid(NbtCompound nbt, CallbackInfo ci) {
        RecipeInputInventory craftingGrid = playerScreenHandler.getCraftingInput();
        NbtList items = new NbtList();
        for (int i = 0; i < craftingGrid.size(); ++i) {
            ItemStack itemStack = craftingGrid.getStack(i);
            if (itemStack.isEmpty()) continue;
            NbtCompound stack = new NbtCompound();
            stack.putByte("Slot", (byte) i);
            itemStack.writeNbt(stack);
            items.add(stack);
        }
        nbt.put("CraftingItems", items);
    }
    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V", ordinal = 0))
    private void onGroundForLonger(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir, @Local ItemEntity itemEntity) {
        int diff = itemEntity.getWorld().getDifficulty().getId();
        if (diff == 2) itemEntity.setCovetedItem();
        if (diff < 2) itemEntity.setNeverDespawn();
    }
}
