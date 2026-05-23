package net.greenjab.fixedminecraft.registry.other;

import net.greenjab.fixedminecraft.registry.registries.MenuRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;

public class FletchingMenu extends AbstractContainerMenu {
    private final Slot flintSlot;
    private final Slot stickSlot;
    private final Slot featherSlot;
    private final Slot potionSlot;
    private final Container resultSlots = new ResultContainer();
    private final Container craftSlots = new SimpleContainer(4) {

        @Override
        public void setChanged() {
            super.setChanged();
            FletchingMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;

    public FletchingMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public FletchingMenu(final int containerId, final Inventory inventory, final ContainerLevelAccess access) {
        super(MenuRegistry.FLETCHING_SCREEN_HANDLER, containerId);
        this.access = access;
        this.addSlot(new Slot(this.resultSlots, 0, 124, 35) {
            @Override public boolean mayPlace(final @NonNull ItemStack itemStack) { return false; }
            @Override public void onTake(final @NonNull Player player, final @NonNull ItemStack carried) {
                FletchingMenu.this.flintSlot.remove(1);
                FletchingMenu.this.stickSlot.remove(1);
                FletchingMenu.this.featherSlot.remove(1);
                FletchingMenu.this.potionSlot.remove(1);
            }
        });

        this.flintSlot = this.addSlot(new Slot(this.craftSlots, 0, 30+4, 17) {
            @Override public boolean mayPlace(final @NonNull ItemStack itemStack) {
                return itemStack.is(Items.FLINT);}});
        this.stickSlot = this.addSlot(new Slot(this.craftSlots, 1, 30-4, 17+18) {
            @Override public boolean mayPlace(final @NonNull ItemStack itemStack)  {
                return itemStack.is(Items.STICK); }});
        this.featherSlot = this.addSlot(new Slot(this.craftSlots, 2, 30-12, 17+2*18) {
            @Override public boolean mayPlace(final @NonNull ItemStack itemStack)  {
                return itemStack.is(Items.FEATHER); }});
        this.potionSlot = this.addSlot(new Slot(this.craftSlots, 3, 30+ 2*18+2, 17+18) {
            @Override public boolean mayPlace(final @NonNull ItemStack itemStack)  {
                return itemStack.is(Items.POTION) || itemStack.is(Items.GLOWSTONE); }});

        this.addStandardInventorySlots(inventory, 8, 84);
    }

    @Override
    public void slotsChanged(@NonNull Container container) {
        super.slotsChanged(container);
        if (container == this.craftSlots) {
            this.createResult();
        }
    }
    private void createResult() {
        this.resultSlots.setItem(0, updateResult(this.craftSlots));
        this.broadcastChanges();
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(this.access, player, Blocks.FLETCHING_TABLE);
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex == 0) {
                stack.getItem().onCraftedBy(stack, player);
                if (!this.moveItemStackTo(stack, 5, 41, true)) return ItemStack.EMPTY;
                slot.onQuickCraft(stack, clicked);
            } else if (slotIndex >= 5 && slotIndex < 41) {
                if (!this.moveItemStackTo(stack, 1, 5, false)) {
                    if (slotIndex < 32) {
                        if (!this.moveItemStackTo(stack, 32, 41, false)) return ItemStack.EMPTY;
                    } else if (!this.moveItemStackTo(stack, 5, 32, false)) return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 5, 41, false)) return ItemStack.EMPTY;

            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
            if (stack.getCount() == clicked.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, stack);
            if (slotIndex == 0) player.drop(stack, false);
        }
        return clicked;
    }

    @Override
    public boolean canTakeItemForPickAll(@NonNull ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    protected static ItemStack updateResult(
            Container craftingInventory
    ) {
        ItemStack itemStack = new ItemStack(Items.ARROW, 8);
        if(craftingInventory.getItem(0).isEmpty() || craftingInventory.getItem(1).isEmpty() ||
                craftingInventory.getItem(2).isEmpty()) { return ItemStack.EMPTY;
        }
        ItemStack extra = craftingInventory.getItem(3);
        if (extra.isEmpty()) return itemStack;
        if (extra.is(Items.GLOWSTONE)) return new ItemStack(Items.SPECTRAL_ARROW, 8);
        if (extra.has(DataComponents.POTION_CONTENTS)) {
            ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW, 8);
            tippedArrow.set(DataComponents.POTION_CONTENTS, extra.get(DataComponents.POTION_CONTENTS));
            return tippedArrow;
        }
        return itemStack;
    }

    @Override
    public void removed(final @NonNull Player player) {
        super.removed(player);
        this.access.execute((_, _) -> this.clearContainer(player, this.craftSlots));
    }

    public Slot getFlintSlot() {
        return this.flintSlot;
    }
    public Slot getStickSlot() {
        return this.stickSlot;
    }
    public Slot getFeatherSlot() {
        return this.featherSlot;
    }
    public Slot getPotionSlot() {
        return this.potionSlot;
    }
}
