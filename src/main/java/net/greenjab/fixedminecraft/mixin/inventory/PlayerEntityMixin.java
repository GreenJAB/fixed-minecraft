package net.greenjab.fixedminecraft.mixin.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Saves player crafting inventory across restarts.
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Shadow
    @Final
    public PlayerScreenHandler playerScreenHandler;

    @Inject(method = "readCustomData", at = @At("RETURN"))
    private void readCraftingGrid(ReadView view, CallbackInfo ci) {
        RecipeInputInventory craftingGrid = playerScreenHandler.getCraftingInput();
        DefaultedList<ItemStack> stacks = ((CraftingInventoryAccessor) craftingGrid).getStacks();
        ReadView.TypedListReadView<StackWithSlot> items = view.getTypedListView("CraftingItems", StackWithSlot.CODEC);
        if (items == null) return;
        stacks.clear();
        for (StackWithSlot stackWithSlot : items) {
            if (stackWithSlot.isValidSlot(4)) {
                stacks.set(stackWithSlot.slot(), stackWithSlot.stack());
            }
        }



        /*RecipeInputInventory craftingGrid = playerScreenHandler.getCraftingInput();
        DefaultedList<ItemStack> stacks = ((CraftingInventoryAccessor) craftingGrid).getStacks();
        NbtList items = nbt.getListOrEmpty("CraftingItems");
        if (items == null) return;
        stacks.clear();
        PlayerEntity PE = (PlayerEntity)(Object)this;
        for (int i = 0; i < items.size(); i++) {
            NbtCompound nbtCompound = items.getCompoundOrEmpty(i);
            int slot = nbtCompound.getByte("Slot", (byte)0) & 255;
            ItemStack itemStack = ItemStack.fromNbt(PE.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
            stacks.set(slot, itemStack);
        }*/


        /** Gave errors even with access widener */
        /*NbtCompound nbtCompound = nbt.getCompound("CraftingResult");
        if (nbtCompound != null && !nbtCompound.isEmpty()) {
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            playerScreenHandler.craftingResult.setStack(0, itemStack);
        }*/
    }

    @Inject(method = "writeCustomData", at = @At("RETURN"))
    private void writeCraftingGrid(WriteView view, CallbackInfo ci) {
        RecipeInputInventory craftingGrid = playerScreenHandler.getCraftingInput();

        WriteView.ListAppender<StackWithSlot> list = view.getListAppender("CraftingItems", StackWithSlot.CODEC);
        for (int i = 0; i < craftingGrid.size(); i++) {
            ItemStack itemStack = craftingGrid.getStack(i);
            if (!itemStack.isEmpty()) {
                list.add(new StackWithSlot(i, itemStack));
            }
        }




        /*RecipeInputInventory craftingGrid = playerScreenHandler.getCraftingInput();

        PlayerEntity PE = (PlayerEntity)(Object)this;
        NbtList items = new NbtList();
        for (int i = 0; i < craftingGrid.size(); i++) {
            if (!craftingGrid.getStack(i).isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                items.add(craftingGrid.getStack(i).toNbt(PE.getRegistryManager(), nbtCompound));
            }
        }


        nbt.put("CraftingItems", items);*/


        /*ItemStack itemStack  = playerScreenHandler.craftingResult.getStack(0);
        if (itemStack!=null) {
            nbt.put("CraftingResult", playerScreenHandler.craftingResult.getStack(0).writeNbt(new NbtCompound()));
        }*/
    }

    @Inject(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V"))
    private void dropCraftingGridItems(ServerWorld world, CallbackInfo ci) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        for (ItemStack itemStack : PE.playerScreenHandler.craftingInventory.getHeldStacks()) {
            PE.dropItem(itemStack, false);
        }
    }

}
