package net.greenjab.fixedminecraft.registry.other;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.greenjab.fixedminecraft.registry.registries.MenuRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
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

    private boolean repairItem;

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

        int finalbreakChance;
        if (this.inputSlots.getItem(1).isEmpty()) {
            finalbreakChance = 0;
        } else if (isNetherite()) {
            int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(carried);
            int current = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(carried, false);
            if (current > cap) finalbreakChance = 12;
            else finalbreakChance = 0;
        } else finalbreakChance = 12;

        ItemStack itemStack = this.inputSlots.getItem(1);
        if (this.repairItem) {
            if (!itemStack.isEmpty()) {
                itemStack.shrink(1);
                this.inputSlots.setItem(1, itemStack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
            if (player instanceof ServerPlayer SPE) {
                CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.ANVIL.getDefaultInstance());
                if (isNetherite()) {
                    int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(carried);
                    int current = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(carried, false);
                    if (current > cap) CriteriaTriggers.CONSUME_ITEM.trigger(SPE, ItemRegistry.NETHERITE_ANVIL.getDefaultInstance());
                }
            }
        } else {
            itemStack.shrink(1);
            this.inputSlots.setItem(1, itemStack);
        }

        this.access.execute((level, pos) -> {
            BlockState blockState = level.getBlockState(pos);
            if (!player.hasInfiniteMaterials() && blockState.is(BlockTags.ANVIL) && player.getRandom().nextFloat()*100 < finalbreakChance) {
                BlockState blockState2 = AnvilBlock.damage(blockState);
                if (blockState2 == null) {
                    level.removeBlock(pos, false);
                    level.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
                } else {
                    level.setBlock(pos, blockState2, Block.UPDATE_CLIENTS);
                    level.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
                }
            } else {
                level.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
            }

        });
        this.inputSlots.setItem(0, ItemStack.EMPTY);
    }

    @Override
    public void createResult() {
        ItemStack input = this.inputSlots.getItem(0);
        ItemStack addition = this.inputSlots.getItem(1);
        ItemStack result = input.copy();

        this.capacity.set(FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(result));

        this.cost.set(0);
        this.text.set(AnvilMsg.NONE.id);
        this.resultSlots.setItem(0, ItemStack.EMPTY);
        if (input.isEmpty()) return;

        boolean newName = false;
        boolean repair = false;
        if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            if (!this.itemName.equals(input.getHoverName().getString())) {
                newName = true;
                result.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
        } else if (input.has(DataComponents.CUSTOM_NAME)) {
            newName = true;
            result.remove(DataComponents.CUSTOM_NAME);
        }

        if (addition.isEmpty()) {
            if (newName) {
                this.resultSlots.setItem(0, result);
                this.text.set(AnvilMsg.NAME.id);
                this.cost.set(1);
            }
            return;
        }

        if (!EnchantmentHelper.canStoreEnchantments(input)) return;

        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
        this.repairItem = false;
        if (!addition.isEmpty()) {
            if (input.is(Items.BOOK)) {
                this.text.set(AnvilMsg.COMBINE.id);
                return;
            }
            boolean book = addition.is(Items.ENCHANTED_BOOK) || addition.is(Items.BOOK);
            //2nd slot are ingots
            if (result.isDamageableItem() && input.isValidRepairItem(addition)) {
                if (input.getDamageValue() == 0) {
                    this.text.set(AnvilMsg.FIXED.id);
                    return;
                }
                repair = true;
                result.setDamageValue(0);
                this.repairItem = true;
            } else {
                //2nd slot isnt usable
                if (!book && (!result.is(addition.getItem()) || !result.isDamageableItem())) {
                    this.text.set(AnvilMsg.COMBINE.id);
                    return;
                }

                if (result.isDamageableItem() && !book) {
                    if (EnchantmentHelper.getEnchantmentsForCrafting(addition).isEmpty() || FixedMinecraft.SERVER.getGameRules().get(GameRuleRegistry.COMBINE_ENCHANTED_ITEMS)) {
                        if (input.getDamageValue() == 0 && !FixedMinecraft.SERVER.getGameRules().get(GameRuleRegistry.COMBINE_ENCHANTED_ITEMS)) {
                            this.text.set(AnvilMsg.FIXED.id);
                            return;
                        }
                        int remaining1 = input.getMaxDamage() - input.getDamageValue();
                        int remaining2 = addition.getMaxDamage() - addition.getDamageValue();
                        int additional = remaining2 + result.getMaxDamage() * 12 / 100;
                        int remaining = remaining1 + additional;
                        int resultDamage = result.getMaxDamage() - remaining;
                        if (resultDamage < 0) resultDamage = 0;

                        if (resultDamage < result.getDamageValue()) {
                            result.setDamageValue(resultDamage);
                            repair = true;
                        }
                    } else {
                        this.text.set(AnvilMsg.COMBINE.id);
                        return;
                    }
                }

                ItemEnchantments itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(addition);
                boolean hasGoodEnchant = false;
                boolean hasBadEnchant = false;

                for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantmentsComponent.entrySet()) {
                    Holder<Enchantment> registryEntry = entry.getKey();
                    int q = builder.getLevel(registryEntry);
                    int r = entry.getIntValue();
                    Enchantment enchantment = registryEntry.value();
                    r = q == r ? r + (book&&r<enchantment.getMaxLevel()?1:0) : Math.max(r, q);

                    boolean canAdd = enchantment.canEnchant(input);
                    if (this.player.hasInfiniteMaterials() || input.is(Items.ENCHANTED_BOOK)) {
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
        EnchantmentHelper.setEnchantments(result, builder.toImmutable());
        int enchantmentPower = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(result, true);
        if (repair) this.cost.set(Mth.ceil(enchantmentPower / 2.0f));
        else this.cost.set(enchantmentPower);


        if (!this.player.hasInfiniteMaterials() && ((enchantmentPower < 1 || enchantmentPower > this.capacity.get()) && this.capacity.get() != 0)) {
            if (!isNetherite()) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
                this.text.set(AnvilMsg.OVER.id);
                return;
            }

            if (!FixedMinecraft.SERVER.getGameRules().get(GameRuleRegistry.MENDING_ON_OP_ITEMS)) {
                ItemEnchantments outputEnchants = EnchantmentHelper.getEnchantmentsForCrafting(result);
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : outputEnchants.entrySet()) {
                    Holder<Enchantment> registryEntry = entry.getKey();
                    if (registryEntry.getRegisteredName().toLowerCase().contains("mending")) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.text.set(AnvilMsg.MENDING.id);
                        return;
                    }
                }
            }
        }

        if (!newName && result.is(Items.ENCHANTED_BOOK)) result.set(DataComponents.REPAIR_COST, 0);
        if (ItemStack.isSameItemSameComponents(input, result)) {
            this.text.set(AnvilMsg.CHANGE.id);
            return;
        }
        this.resultSlots.setItem(0, result);
        if (repair) this.text.set(AnvilMsg.REPAIR.id);
        else this.text.set(AnvilMsg.COST.id);
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
        OVER(5, "over", false),
        CHANGE(6, "change", false),
        NAME(7, "name", true),
        REPAIR(8, "repair", true),
        COST(9, "cost", true);

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
