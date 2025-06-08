package net.greenjab.fixedminecraft.registry.other;

import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class FletchingScreenHandler extends CraftingScreenHandler {
    private final ScreenHandlerContext context;

    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(syncId, playerInventory, context);
        this.context = context;
    }

    private Inventory Inv() {
        SimpleInventory items = new SimpleInventory(1);

        ItemStack itemStack2 = new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1);
        itemStack2.set(DataComponentTypes.CUSTOM_NAME, Text.of("."));
        items.heldStacks.set(0, itemStack2);
        return items;
    }

    @Override
    protected void addInputSlots(int x, int y) {

        this.addSlot(new Slot(this.craftingInventory, 0, x, y){
            @Override public boolean canInsert(ItemStack stack) {
                if (stack == null) return false;
                return stack.isOf(Items.FLINT);
            }
        });
        this.addSlot(new Slot(Inv(), 0, x + 18, y){
            @Override public boolean canTakeItems(PlayerEntity playerEntity) {return false;}});
        this.addSlot(new Slot(Inv(), 0, x + 2*18, y){
            @Override public boolean canTakeItems(PlayerEntity playerEntity) {return false;}});

        this.addSlot(new Slot(this.craftingInventory, 3, x, y + 18){
            @Override public boolean canInsert(ItemStack stack) {
                if (stack == null) return false;
                return stack.isOf(Items.STICK);
            }
        });
        this.addSlot(new Slot(Inv(), 0, x + 18, y + 18){
            @Override public boolean canTakeItems(PlayerEntity playerEntity) {return false;}});

        this.addSlot(new Slot(this.craftingInventory, 5, x + 2*18, y + 18){
            @Override public boolean canInsert(ItemStack stack) {
                if (stack == null) return false;
                return stack.isOf(Items.POTION) || stack.isOf(Items.GLOWSTONE);
            }
        });
        this.addSlot(new Slot(this.craftingInventory, 6, x, y + 2*18){
            @Override public boolean canInsert(ItemStack stack) {
                if (stack == null) return false;
                return stack.isOf(Items.FEATHER);
            }
        });

        this.addSlot(new Slot(Inv(), 0, x + 18, y + 2*18){
            @Override public boolean canTakeItems(PlayerEntity playerEntity) {return false;}});
        this.addSlot(new Slot(Inv(), 0, x + 2*18, y + 2*18){
            @Override public boolean canTakeItems(PlayerEntity playerEntity) {return false;}});
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.context.run( (world, pos) -> {
            if (world instanceof ServerWorld serverWorld) {
                updateResult(this, serverWorld, this.getPlayer(), this.craftingInventory, this.craftingResultInventory);
            }
        });
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, Blocks.FLETCHING_TABLE);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                this.context.run( (world, pos) -> itemStack2.getItem().onCraftByPlayer(itemStack2, world, player));
                if (!this.insertItem(itemStack2, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if (slot >= 10 && slot < 46) {
                if (!this.insertItem(itemStack2, 1, 10, false)) {
                    if (slot < 37) {
                        if (!this.insertItem(itemStack2, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(itemStack2, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.insertItem(itemStack2, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
            if (slot == 0) {
                player.dropItem(itemStack2, false);
            }
        }

        return itemStack;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.craftingResultInventory && super.canInsertIntoSlot(stack, slot);
    }

    protected static void updateResult(
            ScreenHandler handler,
            World world,
            PlayerEntity player,
            RecipeInputInventory craftingInventory,
            CraftingResultInventory resultInventory
    ) {
        if (!world.isClient) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            ItemStack itemStack = ItemStack.EMPTY;
            boolean b = !craftingInventory.getStack(0).isEmpty() && !craftingInventory.getStack(3).isEmpty() &&
                    !craftingInventory.getStack(6).isEmpty();
            if (b) {
                ItemStack extra = craftingInventory.getStack(5);
                if (extra.isEmpty()) itemStack = new ItemStack(Items.ARROW, 16);
                else if (extra.isOf(Items.GLOWSTONE)) itemStack = new ItemStack(Items.SPECTRAL_ARROW, 16);
                else {
                    ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW, 16);
                    tippedArrow.set(DataComponentTypes.POTION_CONTENTS, extra.get(DataComponentTypes.POTION_CONTENTS));
                    itemStack = tippedArrow;
                }
            }

            resultInventory.setStack(0, itemStack);
            handler.setPreviousTrackedSlot(0, itemStack);
            serverPlayerEntity.networkHandler.sendPacket(
                    new ScreenHandlerSlotUpdateS2CPacket(
                            handler.syncId,
                            handler.nextRevision(),
                            0,
                            itemStack
                    )
            );
        }
    }
}
