package net.greenjab.fixedminecraft.registry.other;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.greenjab.fixedminecraft.registry.registries.MenuRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public class NewAnvilMenu extends ItemCombinerMenu {

    @Nullable
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();
    private final DataSlot capacity = DataSlot.standalone();
    private final DataSlot netherite = DataSlot.standalone();
    private final DataSlot text = DataSlot.standalone();
    private static final int INPUT_SLOT_X_PLACEMENT = 27;
    private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
    private static final int RESULT_SLOT_X_PLACEMENT = 134;
    private static final int SLOT_Y_PLACEMENT = 47;

    private int repairItemUsage;

    public NewAnvilMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL, false);
    }

    public NewAnvilMenu(final int containerId, final Inventory inventory, final ContainerLevelAccess access, boolean netherite) {
        super(MenuRegistry.NEW_ANVIL_SCREEN_HANDLER, containerId, inventory, access, createInputSlotDefinitions());
        this.addDataSlot(this.cost);
        this.addDataSlot(this.capacity);
        this.addDataSlot(this.netherite).set(netherite?1:0);
        this.addDataSlot(this.text).set(0);
    }

    private static ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, INPUT_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, _ -> true)
                .withSlot(1, ADDITIONAL_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, _ -> true)
                .withResultSlot(2, RESULT_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT)
                .build();
    }

    @Override
    protected boolean isValidBlock(final BlockState state) {
        return state.is(BlockTags.ANVIL);
    }

    @Unique
    ItemStack anvilHolder = ItemStack.EMPTY;

    @Override
    protected boolean mayPickup(final @NonNull Player player, final boolean hasItem) {
        if (!this.resultSlots.getItem(0).isEmpty())
            anvilHolder = this.resultSlots.getItem(0).copy();
        return (player.hasInfiniteMaterials() || player.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    @Override
    protected void onTake(final @NonNull Player player, @NonNull ItemStack carried) {
        if (carried.isEmpty()) carried = anvilHolder;
        if (!player.hasInfiniteMaterials()) {
            player.giveExperienceLevels(-this.cost.get());
        }

        int superEnchants = 0;
        ItemEnchantments map = EnchantmentHelper.getEnchantmentsForCrafting(carried);
        for (Holder<Enchantment> enchantment : map.keySet()) {
            int l1 = map.getLevel(enchantment);
            boolean isGold = carried.is(ItemTags.PIGLIN_LOVED);
            if (l1 > enchantment.value().getMaxLevel() && !isGold) {
                superEnchants++;
            }
        }

        int breakChance = 0;
        if (isNetherite()) {
            int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(carried);
            int current = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(carried, false);
            if (current > cap) {
                breakChance = 10;
            }
        }
        final int finalbreakChance = breakChance + 5*superEnchants;

        ItemStack itemStack = this.inputSlots.getItem(1);
        if (this.repairItemUsage > 0) {
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.shrink(this.repairItemUsage);
                this.inputSlots.setItem(1, itemStack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
            if (player instanceof ServerPlayer SPE) {
                CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.ANVIL.getDefaultInstance());
                if (isNetherite()) {
                    boolean isSuper = false;
                    if (carried.getComponents().has(DataComponents.REPAIR_COST)) {
                        isSuper = carried.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) == 1;
                    }
                    if (isSuper) {
                        CriteriaTriggers.CONSUME_ITEM.trigger(SPE, ItemRegistry.NETHERITE_ANVIL.getDefaultInstance());
                    }
                }
            }
        } else {
            itemStack.shrink(1);
            this.inputSlots.setItem(1, itemStack);
        }

        this.access.execute((world, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            if (!player.hasInfiniteMaterials() && blockState.is(BlockTags.ANVIL) && player.getRandom().nextFloat()*100 < finalbreakChance) {
                BlockState blockState2 = AnvilBlock.damage(blockState);
                if (blockState2 == null) {
                    world.removeBlock(pos, false);
                    world.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
                } else {
                    world.setBlock(pos, blockState2, Block.UPDATE_CLIENTS);
                    world.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
                }
            } else {
                world.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
            }

        });
        this.inputSlots.setItem(0, ItemStack.EMPTY);
    }

    @Override
    public void createResult() {
        ItemStack firstInputStack = this.inputSlots.getItem(0);
        ItemStack secondInputStack = this.inputSlots.getItem(1);
        ItemStack outputItemStack = firstInputStack.copy();

        this.capacity.set(FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(outputItemStack));

        this.cost.set(0);
        this.text.set(AnvilMsg.NONE.id);
        this.resultSlots.setItem(0, ItemStack.EMPTY);
        if (firstInputStack.isEmpty()) return;

        boolean newName = false;
        boolean repair = false;
        if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            if (!this.itemName.equals(firstInputStack.getHoverName().getString())) {
                newName = true;
                outputItemStack.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
        } else if (firstInputStack.has(DataComponents.CUSTOM_NAME)) {
            newName = true;
            outputItemStack.remove(DataComponents.CUSTOM_NAME);
        }

        if (secondInputStack.isEmpty()) {
            if (newName) {
                this.resultSlots.setItem(0, outputItemStack);
                this.text.set(AnvilMsg.NAME.id);
                this.cost.set(1);
            }
            return;
        }

        if (!EnchantmentHelper.canStoreEnchantments(firstInputStack)) return;

        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(outputItemStack));
        this.repairItemUsage = 0;
        if (!secondInputStack.isEmpty()) {
            boolean ebook = secondInputStack.is(Items.ENCHANTED_BOOK);
            boolean book2 = ebook || secondInputStack.is(Items.BOOK);
            //2nd slot are ingots
            if (outputItemStack.isDamageableItem() && firstInputStack.isValidRepairItem(secondInputStack)) {
                if (firstInputStack.getDamageValue() == 0) {
                    this.text.set(AnvilMsg.FIXED.id);
                    return;
                }
                int k = Math.min(outputItemStack.getDamageValue(), outputItemStack.getMaxDamage() / 2);
                int m;
                for (m = 0; k > 0 && m < secondInputStack.getCount(); m++) {
                    int n = outputItemStack.getDamageValue() - k;
                    outputItemStack.setDamageValue(n);
                    k = Math.min(outputItemStack.getDamageValue(), outputItemStack.getMaxDamage() / 2);
                }
                repair = true;
                this.repairItemUsage = m;
            } else {
                //2nd slot isnt usable
                if (!book2 && (!outputItemStack.is(secondInputStack.getItem()) || !outputItemStack.isDamageableItem())) {
                    this.text.set(AnvilMsg.COMBINE.id);
                    return;
                }

                if (outputItemStack.isDamageableItem() && !book2) {
                    if (EnchantmentHelper.getEnchantmentsForCrafting(secondInputStack).isEmpty()) {
                        if (firstInputStack.getDamageValue() == 0) {
                            this.text.set(AnvilMsg.FIXED.id);
                            return;
                        }
                        int kx = firstInputStack.getMaxDamage() - firstInputStack.getDamageValue();
                        int m = secondInputStack.getMaxDamage() - secondInputStack.getDamageValue();
                        int n = m + outputItemStack.getMaxDamage() * 12 / 100;
                        int o = kx + n;
                        int p = outputItemStack.getMaxDamage() - o;
                        if (p < 0) {
                            p = 0;
                        }

                        if (p < outputItemStack.getDamageValue()) {
                            outputItemStack.setDamageValue(p);
                            repair = true;
                        }
                    } else {
                        this.text.set(AnvilMsg.COMBINE.id);
                        return;
                    }
                }

                if (book2) {
                    ItemEnchantments itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(secondInputStack);
                    boolean hasGoodEnchant = false;
                    boolean hasBadEnchant = false;

                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantmentsComponent.entrySet()) {
                        Holder<Enchantment> registryEntry = entry.getKey();
                        int q = builder.getLevel(registryEntry);
                        int r = entry.getIntValue();
                        Enchantment enchantment = registryEntry.value();
                        r = q == r ? r + (ebook&&r<enchantment.getMaxLevel()?1:0) : Math.max(r, q);

                        boolean canAdd = enchantment.canEnchant(firstInputStack);
                        if (this.player.hasInfiniteMaterials() || firstInputStack.is(Items.ENCHANTED_BOOK)) {
                            canAdd = true;
                        }

                        for (Holder<Enchantment> registryEntry2 : builder.keySet()) {
                            if (!registryEntry2.equals(registryEntry) && !Enchantment.areCompatible(registryEntry, registryEntry2)) {
                                canAdd = false;
                            }
                        }

                        if (!canAdd) hasBadEnchant = true;
                        else {
                            hasGoodEnchant = true;
                            builder.set(registryEntry, r);
                        }
                    }

                    if (hasBadEnchant && !hasGoodEnchant) {
                        this.text.set(AnvilMsg.ENCHANT.id);
                        return;
                    }
                }
            }
        }
        EnchantmentHelper.setEnchantments(outputItemStack, builder.toImmutable());
        if (isNetherite()) {
            ItemEnchantments outputEnchants = EnchantmentHelper.getEnchantmentsForCrafting(outputItemStack);
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : outputEnchants.entrySet()) {
                Holder<Enchantment> registryEntry = entry.getKey();
                if (registryEntry.getRegisteredName().toLowerCase().contains("mending")) {
                    builder.set(registryEntry, 0);
                    EnchantmentHelper.setEnchantments(outputItemStack, builder.toImmutable());
                }
            }
            if (secondInputStack.is(Items.ENCHANTED_BOOK)) {
                ItemEnchantments bookEnchants = EnchantmentHelper.getEnchantmentsForCrafting(secondInputStack);
                if (bookEnchants.keySet().size() == 1) {
                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : bookEnchants.entrySet()) {
                        Holder<Enchantment> registryEntry = entry.getKey();
                        if (registryEntry.getRegisteredName().toLowerCase().contains("mending")) {
                            this.text.set(AnvilMsg.MENDING.id);
                            return;
                        }
                    }
                }
            }
        }
        int enchantmentPower = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(outputItemStack, true);
        if (repair) this.cost.set(Mth.ceil(enchantmentPower / 2.0f));
        else this.cost.set(enchantmentPower);

        if (!isNetherite()) {
            if (!this.player.hasInfiniteMaterials()) {
                boolean isSuper = false;
                if (outputItemStack.getComponents().has(DataComponents.REPAIR_COST)) {
                    isSuper = outputItemStack.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) ==1;
                }

                if (outputItemStack.is(ItemTags.PIGLIN_LOVED)) {
                    isSuper = false;
                }

                if ((enchantmentPower < 1 || this.capacity.get() < enchantmentPower) && this.capacity.get() != 0 || isSuper) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.text.set(isSuper?AnvilMsg.SUPER.id:AnvilMsg.OVER.id);
                    return;
                }
            }
        }
        if (!newName && outputItemStack.is(Items.ENCHANTED_BOOK)) {
            outputItemStack.set(DataComponents.REPAIR_COST, 0);
        }
        if (ItemStack.isSameItemSameComponents(firstInputStack, outputItemStack)) {
            this.text.set(AnvilMsg.CHANGE.id);
            return;
        }
        this.resultSlots.setItem(0, outputItemStack);
        if (repair) {
            this.text.set(AnvilMsg.REPAIR.id);

        } else {
            this.text.set(AnvilMsg.COST.id);
        }
    }


    public boolean setItemName(final String name) {
        String validatedName = validateName(name);
        if (validatedName != null && !validatedName.equals(this.itemName)) {
            this.itemName = validatedName;
            if (this.getSlot(2).hasItem()) {
                ItemStack itemStack = this.getSlot(2).getItem();
                if (StringUtil.isBlank(validatedName)) {
                    itemStack.remove(DataComponents.CUSTOM_NAME);
                } else {
                    itemStack.set(DataComponents.CUSTOM_NAME, Component.literal(validatedName));
                }
            }

            this.createResult();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private static String validateName(final String name) {
        String filteredName = StringUtil.filterText(name);
        return filteredName.length() <= 50 ? filteredName : null;
    }

    public int getCost() {
        return this.cost.get();
    }
    public int getCapacity() {
        return this.capacity.get();
    }
    public boolean isNetherite() {
        return this.netherite.get()==1;
    }
    public int getText() {
        return this.text.get();
    }

    public enum AnvilMsg {

        NONE(0, "", false),
        FIXED(1, "fixed", false),
        COMBINE(2, "combine", false),
        ENCHANT(3, "enchant", false),
        MENDING(4, "mending", false),
        SUPER(5, "super", false),
        OVER(6, "over", false),
        CHANGE(7, "change", false),
        NAME(8, "name", true),
        REPAIR(9, "repair", true),
        COST(10, "cost", true);

        public final int id;
        public final String lang;
        public final boolean includeCost;
        AnvilMsg(int id, String lang, boolean includeCost){
            this.id = id;
            this.lang = lang;
            this.includeCost = includeCost;
        }

        public static AnvilMsg byID(int id) {
            for (AnvilMsg msg : AnvilMsg.values()){
                if (msg.id == id) return msg;
            }
            return NONE;
        }
    }

}
