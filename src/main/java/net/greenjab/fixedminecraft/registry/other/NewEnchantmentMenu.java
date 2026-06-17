package net.greenjab.fixedminecraft.registry.other;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.registry.registries.MenuRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.NonInteractiveResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class NewEnchantmentMenu extends AbstractContainerMenu {
    private static final Identifier EMPTY_SLOT_LAPIS_LAZULI = Identifier.withDefaultNamespace("container/slot/lapis_lazuli");
    private final Container enchantSlots = new SimpleContainer(2){
        @Override
        public void setChanged() {
            super.setChanged();
            NewEnchantmentMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;
    private final RandomSource random = RandomSource.create();
    private final DataSlot enchantmentSeed = DataSlot.standalone();
    public final int[] costs = new int[3];
    public final int[] icon = new int[]{-1, -1, -1};
    private final Container outcomeSlots = new SimpleContainer(3);

    public NewEnchantmentMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public NewEnchantmentMenu(final int containerId, final Inventory inventory, final ContainerLevelAccess access) {
        super(MenuRegistry.NEW_ENCHANTMENT_SCREEN_HANDLER, containerId);
        this.access = access;
        this.addSlot(new Slot(this.enchantSlots, 0, 15, 47) {
            @Override public int getMaxStackSize() { return 1; }});
        this.addSlot(new Slot(this.enchantSlots, 1, 35, 47) {
            @Override public boolean mayPlace(final @NonNull ItemStack itemStack) { return itemStack.is(Items.LAPIS_LAZULI); }
            @Override public Identifier getNoItemIcon() { return NewEnchantmentMenu.EMPTY_SLOT_LAPIS_LAZULI; }});
        this.addSlot(new NonInteractiveResultSlot(this.outcomeSlots, 0, 151, 17));
        this.addSlot(new NonInteractiveResultSlot(this.outcomeSlots, 1, 151, 35));
        this.addSlot(new NonInteractiveResultSlot(this.outcomeSlots, 2, 151, 53));
        this.addStandardInventorySlots(inventory, 8, 84);
        this.addDataSlot(DataSlot.shared(this.costs, 0));
        this.addDataSlot(DataSlot.shared(this.costs, 1));
        this.addDataSlot(DataSlot.shared(this.costs, 2));
        this.addDataSlot(DataSlot.shared(this.icon, 0));
        this.addDataSlot(DataSlot.shared(this.icon, 1));
        this.addDataSlot(DataSlot.shared(this.icon, 2));
        this.addDataSlot(this.enchantmentSeed).set(inventory.player.getEnchantmentSeed());
    }

    @Override
    public void slotsChanged(final @NonNull Container container) {
        if (container == this.enchantSlots) {
            ItemStack itemStack = container.getItem(0);
            if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
                this.access.execute( (level, pos) -> {

                    int bookShelfCount = FixedMinecraftEnchantmentHelper.countAccessibleBookshelves(level, pos);
                    int power = (int)((FixedMinecraftEnchantmentHelper.POWER_WHEN_MAX_LEVEL-1) * bookShelfCount / 15f + 1);

                    for (int slot = 0; slot < 3; slot++) {

                        List<EnchantmentInstance> enchantments;
                        enchantments = this.getEnchantmentList(level.registryAccess(), itemStack, slot, power);
                        if (!enchantments.isEmpty()) {

                            ItemStack display = this.enchantSlots.getItem(0).copy();
                            enchantments.forEach(enchantment -> display.enchant(enchantment.enchantment(), enchantment.level()));
                            if (enchantments.stream().anyMatch(enchantment -> enchantment.level() > enchantment.enchantment().value().getMaxLevel())) display.set(DataComponents.REPAIR_COST, 1);
                            this.outcomeSlots.setItem(slot, display);

                            int enchantmentPower = 0;
                            for (EnchantmentInstance entry : enchantments) {
                                enchantmentPower += FixedMinecraftEnchantmentHelper.getEnchantmentPower(entry.enchantment(), entry.level());
                            }
                            int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(itemStack);
                            int img = (enchantmentPower>cap)?2:((enchantmentPower>cap/2)?1:0);
                            this.icon[slot] = img; // again, for display purposes only

                            boolean isGold = itemStack.is(ItemTags.PIGLIN_LOVED);
                            if (isGold) enchantmentPower /= 2;

                            this.costs[slot] = enchantmentPower;
                        } else {
                            for (int i = 0; i < 3; i++) {
                                this.costs[i] = 0;
                                this.icon[i] = -1;
                            }
                        }
                    }
                    this.broadcastChanges();
                });
            } else {
                for (int i = 0; i < 3; i++) {
                    this.costs[i] = 0;
                    this.icon[i] = -1;
                    this.outcomeSlots.setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public boolean clickMenuButton(final @NonNull Player player, final int buttonId) {
        if (buttonId >= 0 && buttonId < this.costs.length) {
            ItemStack itemStack = this.enchantSlots.getItem(0);
            ItemStack currency = this.enchantSlots.getItem(1);
            int enchantmentCost = Mth.ceil(this.costs[buttonId] / 10.0);
            if ((currency.isEmpty() || currency.getCount() < enchantmentCost) && !player.hasInfiniteMaterials()) {
                return false;
            } else if (this.costs[buttonId] <= 0
                       || itemStack.isEmpty()
                       || (player.experienceLevel < enchantmentCost || player.experienceLevel < this.costs[buttonId]) && !player.hasInfiniteMaterials()) {
                return false;
            } else {
                this.access.execute(/* lambda$clickMenuButton$0 */ (level, pos) -> {
                    player.onEnchantmentPerformed(itemStack, enchantmentCost);

                    this.enchantSlots.setItem(0, this.outcomeSlots.getItem(buttonId).copy());

                    currency.consume(enchantmentCost, player);
                    if (currency.isEmpty()) {
                        this.enchantSlots.setItem(1, ItemStack.EMPTY);
                    }

                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, itemStack, enchantmentCost);
                    }

                    this.enchantSlots.setChanged();
                    this.enchantmentSeed.set(player.getEnchantmentSeed());
                    this.slotsChanged(this.enchantSlots);
                    level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                });
                return true;
            }
        } else {
            Util.logAndPauseIfInIde(player.getPlainTextName() + " pressed invalid button id: " + buttonId);
            return false;
        }
    }

    private List<EnchantmentInstance> getEnchantmentList(final RegistryAccess access, final ItemStack itemStack, final int slot, final int enchantmentCost) {
        this.random.setSeed(this.enchantmentSeed.get() + slot + itemStack.getItem().hashCode());
        Optional<HolderSet.Named<Enchantment>> tag = access.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (tag.isEmpty()) {
            return List.of();
        } else {
            List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, itemStack, enchantmentCost, ((HolderSet.Named)tag.get()).stream());
            if (list.isEmpty()) return list;

            List<ItemStack> chosenItemStacks = new ArrayList<>();
            this.access.execute((world, tablePos) -> {
                int bookShelfCount = FixedMinecraftEnchantmentHelper.countAccessibleBookshelves(world, tablePos);
                for (int i=0; i<bookShelfCount; i++) {
                    int randomIndex = this.random.nextInt(EnchantingTableBlock.BOOKSHELF_OFFSETS.size());
                    if (!FixedMinecraftEnchantmentHelper.canAccessBlock(world, tablePos, EnchantingTableBlock.BOOKSHELF_OFFSETS.get(randomIndex), Blocks.CHISELED_BOOKSHELF)) continue;

                    ChiseledBookShelfBlockEntity chiseledBookShelfEntity = (ChiseledBookShelfBlockEntity) world.getBlockEntity(tablePos.offset(EnchantingTableBlock.BOOKSHELF_OFFSETS.get(randomIndex)));
                    if (chiseledBookShelfEntity == null || chiseledBookShelfEntity.isEmpty()) continue;

                    int nextRoll = this.random.nextInt(chiseledBookShelfEntity.getContainerSize());
                    ItemStack itemStackAtRandomSlot = chiseledBookShelfEntity.getItem(nextRoll);
                    if (itemStackAtRandomSlot.isEmpty() || !itemStackAtRandomSlot.is(Items.ENCHANTED_BOOK)) continue;

                    //don't count ebooks that have been taken out and placed back in
                    if (itemStackAtRandomSlot.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) ==2) continue;

                    chosenItemStacks.add(itemStackAtRandomSlot);
                }
            });

            Map<Holder<Enchantment>, Integer> enchantments = new HashMap<>();
            EnchantmentInstance randomEntry = list.get(this.random.nextInt(list.size()));
            enchantments.put(randomEntry.enchantment(), randomEntry.level());

            AtomicInteger enchPower = new AtomicInteger();
            enchPower.addAndGet(FixedMinecraftEnchantmentHelper.getEnchantmentPower(randomEntry.enchantment(), randomEntry.level()));

            for (ItemStack chosenStack : chosenItemStacks) {
                ItemEnchantments bookEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(chosenStack);
                Map<Holder<Enchantment>, Integer> enchantments2 = new HashMap<>();

                //figure out valid enchants
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : bookEnchantments.entrySet()) {
                    Holder<Enchantment> registryEntry = entry.getKey();
                    Enchantment enchantment = registryEntry.value();
                    int level = entry.getIntValue();
                    if (!((registryEntry.value()).canEnchant(itemStack) || itemStack.is(Items.BOOK))) continue;
                    if ((enchPower.get() + FixedMinecraftEnchantmentHelper.getEnchantmentPower(registryEntry, level)) <= 0) continue;
                    if (enchantments.containsKey(registryEntry)) {
                        if (enchantments.get(registryEntry) == level) {
                            enchantments2.put(registryEntry, Math.min(level+1, enchantment.getMaxLevel()));
                        } else enchantments2.put(registryEntry, Math.min(level, enchantment.getMaxLevel()));
                        continue;
                    }
                    if (enchantments.keySet().stream().anyMatch(enchantment3 -> !Enchantment.areCompatible(registryEntry, enchantment3))) continue;

                    enchantments2.put(registryEntry, level);
                }

                //select 1 valid enchant
                if (!enchantments2.isEmpty()) {
                    int rand = this.random.nextInt(enchantments2.size());
                    final int[] i = {0};
                    enchantments2.forEach((enchantment, level) -> {
                        if (i[0] == rand) {
                            enchPower.addAndGet(FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, level));
                            enchantments.put(enchantment, level);
                            i[0]++;
                        }
                    });

                }
            }
            if (itemStack.is(Items.BOOK)) enchantments.replaceAll((e, _) -> Math.max(enchantments.get(e) / 2, 1));
            List<EnchantmentInstance> enchantmentsResult = new ArrayList<>();
            enchantments.forEach((enchantment, level) -> enchantmentsResult.add(new EnchantmentInstance(enchantment, (itemStack.is(ItemTags.PIGLIN_LOVED)&&enchantment.value().getMaxLevel()!=1)?level+1:level)));
            return (enchantmentsResult);

        }

    }

    public int getLapisCount() {
        ItemStack goldStack = this.enchantSlots.getItem(1);
        return goldStack.isEmpty() ? 0 : goldStack.getCount();
    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed.get();
    }

    @Override
    public void removed(final @NonNull Player player) {
        super.removed(player);
        this.access.execute( (_, _) -> this.clearContainer(player, this.enchantSlots));
    }

    @Override
    public boolean stillValid(final @NonNull Player player) {
        return stillValid(this.access, player, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public @NonNull ItemStack quickMoveStack(final @NonNull Player player, final int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex == 0) {
                if (!this.moveItemStackTo(stack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex == 1) {
                if (!this.moveItemStackTo(stack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(Items.LAPIS_LAZULI)) {
                if (!this.moveItemStackTo(stack, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.getFirst().hasItem() || !this.slots.getFirst().mayPlace(stack)) {
                    return ItemStack.EMPTY;
                }

                ItemStack singleItem = stack.copyWithCount(1);
                stack.shrink(1);
                this.slots.getFirst().setByPlayer(singleItem);
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return clicked;
    }
}
