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
import net.minecraft.server.world.ServerWorld;
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
        PlayerEntity PE = (PlayerEntity)(Object)this;
        for (int i = 0; i < items.size(); i++) {
            NbtCompound nbtCompound = items.getCompound(i);
            int slot = nbtCompound.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromNbt(PE.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
            stacks.set(slot, itemStack);
        }


        /** Gave errors even with access widener */
        /*NbtCompound nbtCompound = nbt.getCompound("CraftingResult");
        if (nbtCompound != null && !nbtCompound.isEmpty()) {
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            playerScreenHandler.craftingResult.setStack(0, itemStack);
        }*/
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeCraftingGrid(NbtCompound nbt, CallbackInfo ci) {
        RecipeInputInventory craftingGrid = playerScreenHandler.getCraftingInput();

        PlayerEntity PE = (PlayerEntity)(Object)this;
        NbtList items = new NbtList();
        for (int i = 0; i < craftingGrid.size(); i++) {
            if (!craftingGrid.getStack(i).isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                items.add(craftingGrid.getStack(i).encode(PE.getRegistryManager(), nbtCompound));
            }
        }


        nbt.put("CraftingItems", items);
        /*ItemStack itemStack  = playerScreenHandler.craftingResult.getStack(0);
        if (itemStack!=null) {
            nbt.put("CraftingResult", playerScreenHandler.craftingResult.getStack(0).writeNbt(new NbtCompound()));
        }*/
    }

    @Inject(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V"))
    private void dropCraftingGridItems(CallbackInfo ci) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        for (ItemStack itemStack : PE.playerScreenHandler.getCraftingInput().getHeldStacks()) {
            PE.dropItem(itemStack, false);
        }
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V", ordinal = 0))
    private void onGroundForLonger(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir,
                                   @Local ItemEntity itemEntity) {
        int diff = itemEntity.getWorld().getDifficulty().getId();
        if (diff == 2) itemEntity.setCovetedItem();
        if (diff < 2) itemEntity.setNeverDespawn();
    }

}
