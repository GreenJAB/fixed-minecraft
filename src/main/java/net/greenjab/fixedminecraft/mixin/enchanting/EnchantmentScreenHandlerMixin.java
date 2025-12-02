package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/** Credits: Laazuli*/
@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin extends ScreenHandler {

    protected EnchantmentScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Shadow
    protected abstract List<EnchantmentLevelEntry> generateEnchantments(DynamicRegistryManager registryManager, ItemStack stack, int slot, int level);

    @Shadow
    @Final
    public int[] enchantmentId;

    @Shadow
    @Final
    private Random random;

    @Shadow
    @Final
    public int[] enchantmentLevel;

    @Shadow
    @Final
    public int[] enchantmentPower;

    @Shadow
    @Final
    private ScreenHandlerContext context;

    @Shadow
    @Final
    private Inventory inventory;
    @Shadow
    @Final
    private Property seed;


    @Shadow
    public abstract boolean onButtonClick(PlayerEntity player, int id);

    @Unique
    private final List<EnchantmentLevelEntry>[] fixed_minecraft__enchantments = new List[3];


    /**
     * Change logic how enchantments are generated.
     * <br>
     * 1. Takes the first entry of the randomly generated list. Since it already is randomly sorted there is no need to pick a random one from the list
     * 2. Checks for an accessible chiseled bookshelf nearby as often as there are regular bookshelves (limit 15).
     * 3. Chooses a random slot and adds the enchantments to the output list only if the slot contains an enchanted book and the enchantment is suitable for the item in the enchanting table
     */
    @Inject(method = "generateEnchantments", at = @At("RETURN"), cancellable = true)
    private void generateEnchantmentsWithChiseledBookshelves(DynamicRegistryManager registryManager, ItemStack stack, int slot, int xpLevel,
                                                             CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> originalReturnValue = cir.getReturnValue();


        if (originalReturnValue == null || originalReturnValue.isEmpty()) {
            return;
        }
        // select random ItemStacks from a random slot from a random chiseled bookshelf and add to list
        List<ItemStack> chosenItemStacks = new ArrayList<>();

        this.context.run((world, tablePos) -> {
            // count bookshelves
            int bookShelfCount = FixedMinecraftEnchantmentHelper.countAccessibleBookshelves(world, tablePos);
            // scan for chiseled bookshelves [bookShelfCount] times
            for (int i=0; i<bookShelfCount; i++) {

                // choose random index and check accessibility
                int randomIndex = this.random.nextInt(EnchantingTableBlock.POWER_PROVIDER_OFFSETS.size());
                if (!FixedMinecraftEnchantmentHelper.canAccessBlock(world, tablePos, EnchantingTableBlock.POWER_PROVIDER_OFFSETS.get(randomIndex), Blocks.CHISELED_BOOKSHELF)) {
                    continue;
                }

                ChiseledBookshelfBlockEntity chiseledBookShelfEntity = (ChiseledBookshelfBlockEntity) world.getBlockEntity(tablePos.add(EnchantingTableBlock.POWER_PROVIDER_OFFSETS.get(randomIndex)));

                if (chiseledBookShelfEntity == null) {
                    continue;
                }
                if (chiseledBookShelfEntity.isEmpty()) {
                    continue;
                }

                // choose random slot
                int nextRoll = this.random.nextInt(chiseledBookShelfEntity.size());
                ItemStack itemStackAtRandomSlot = chiseledBookShelfEntity.getStack(nextRoll);

                // check whether ItemStack is not null nor empty and is enchanted book
                if (itemStackAtRandomSlot == null || itemStackAtRandomSlot.isEmpty() || !itemStackAtRandomSlot.isOf(Items.ENCHANTED_BOOK)) {
                    continue;
                }

                if (itemStackAtRandomSlot.getComponents().contains(DataComponentTypes.REPAIR_COST)) {
                    if (itemStackAtRandomSlot.getComponents().getOrDefault(DataComponentTypes.REPAIR_COST, 0) ==2) continue;
                }

                chosenItemStacks.add(itemStackAtRandomSlot);
            }
        });

        // take random enchantment out of result
        Map<RegistryEntry<Enchantment>, Integer> enchantments = new HashMap<>();
        EnchantmentLevelEntry randomEntry = originalReturnValue.get(this.random.nextInt(originalReturnValue.size()));
        enchantments.put(randomEntry.enchantment(), randomEntry.level());

        // apply enchantments
        AtomicInteger enchPower = new AtomicInteger();
        enchPower.addAndGet(FixedMinecraftEnchantmentHelper.getEnchantmentPower(randomEntry.enchantment(), randomEntry.level()));

        for (ItemStack chosenStack : chosenItemStacks) {
            ItemEnchantmentsComponent bookEnchantments = EnchantmentHelper.getEnchantments(chosenStack);
            Map<RegistryEntry<Enchantment>, Integer> enchantments2 = new HashMap<>();

            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : bookEnchantments.getEnchantmentEntries()) {
                RegistryEntry<Enchantment> registryEntry = entry.getKey();
                Enchantment enchantment = registryEntry.value();
                int level = entry.getIntValue();
                // ensure enchantment fits on item
                if (!((registryEntry.value()).isAcceptableItem(stack) || stack.isOf(Items.BOOK))) {
                    continue;
                }
                // ensure highest level found is applied; thanks to the map's behaviour, no enchantment will appear more than once
                if (enchantments.containsKey(registryEntry)) {
                    if (enchantments.get(registryEntry) > level) {
                        continue;
                    } else if (enchantments.get(registryEntry) == level) {
                        enchantments2.put(registryEntry, Math.min(level+1, enchantment.getMaxLevel()));
                        continue;
                    }
                    continue;
                }
                // prevent negative or 0 enchantment power
                if ((enchPower.get() + FixedMinecraftEnchantmentHelper.getEnchantmentPower(registryEntry, level)) <= 0) {
                    continue;
                }

                enchantments2.put(registryEntry, level);
            }

            if (!enchantments2.isEmpty()) {
                int rand = this.random.nextInt(enchantments2.size());
                final int[] i = {0};
                enchantments2.forEach((enchantment, level) -> {
                    if (i[0] == rand) {
                        boolean can = true;
                        for (RegistryEntry<Enchantment> enchantment3 : enchantments.keySet()) {
                            if (!Enchantment.canBeCombined(enchantment, enchantment3)) {
                                can = false;
                                break;
                            }
                        }
                        if (can) {
                            enchPower.addAndGet(FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, level));
                            enchantments.put(enchantment, level);
                        }
                        i[0]++;
                    }
                });

            }
        }
        if (stack.isOf(Items.BOOK)) {
            enchantments.replaceAll((e, v) -> Math.max(enchantments.get(e) / 2, 1));
        }
        boolean isGold = stack.isIn(ItemTags.PIGLIN_LOVED);
        // wrap in list and return
        List<EnchantmentLevelEntry> enchantmentsResult = new ArrayList<>();
        enchantments.forEach((enchantment, level) -> enchantmentsResult.add(new EnchantmentLevelEntry(enchantment, (isGold&&enchantment.value().getMaxLevel()!=1)?level+1:level)));
        cir.setReturnValue(enchantmentsResult);
    }

    /**
     * Rewrite logic for generating enchantments
     * <br>
     * 1. Count nearby accessible bookshelves
     * 2. Generate enchantments taking chiseled bookshelves into account
     * 3. Save enchantments into fixed_minecraft__enchantments and assign the first generated enchantment to the designated display properties
     * 4. Send content changes to sync client
     */
    @ModifyArg(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<World, BlockPos> generateEntries(BiConsumer<World, BlockPos> original, @Local ItemStack itemStack) {

        return ((world, blockPos) -> {

            // count nearby bookshelves
            int bookShelfCount = FixedMinecraftEnchantmentHelper.countAccessibleBookshelves(world, blockPos);
            int power = (int)((FixedMinecraftEnchantmentHelper.POWER_WHEN_MAX_LEVEL-1) * bookShelfCount / 15f + 1);
            IndexedIterable<RegistryEntry<Enchantment>> indexedIterable = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getIndexedEntries();

            // generate enchantments for each slot
            for (int slot = 0; slot < 3; slot++) {

                // generate enchantments
                List<EnchantmentLevelEntry> enchantments;
                enchantments = this.generateEnchantments(world.getRegistryManager(), itemStack, slot, power);
                if (!enchantments.isEmpty()) {
                    // set displayed enchantment
                    EnchantmentLevelEntry displayedEnchantment = enchantments.get(0);

                    this.enchantmentId[slot] = indexedIterable.getRawId(displayedEnchantment.enchantment()); // the one that's being displayed


                    // calculate enchantment power
                    int enchantmentPower = 0;
                    for (EnchantmentLevelEntry entry : enchantments) {
                        enchantmentPower += FixedMinecraftEnchantmentHelper.getEnchantmentPower(entry.enchantment(), entry.level());
                    }
                    int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(itemStack);
                    int img = (enchantmentPower>cap)?2:((enchantmentPower>cap/2)?1:0);
                    this.enchantmentLevel[slot] = img; // again, for display purposes only

                    boolean isGold = itemStack.isIn(ItemTags.PIGLIN_LOVED);
                    if (isGold) enchantmentPower /= 2;

                    // set power for display purposes
                    this.enchantmentPower[slot] = enchantmentPower;

                    // save generated enchantments
                    this.fixed_minecraft__enchantments[slot] = enchantments;
                } else {
                    for (int i = 0; i < 3; i++) {
                        this.enchantmentPower[i] = 0;
                        this.enchantmentId[i] = -1;
                        this.enchantmentLevel[i] = -1;
                    }
                }
            }

            // send changes
            this.sendContentUpdates();
        });
    }

    /**
     * Rewrites logic for applying enchantments upon menu click
     * <br>
     * - applies enchantments to ItemStack
     * - applies enchanting costs to player
     * - decrement lapislazuli; reimplemented from vanilla replacing original values with local values
     * - applies statistic changes; reimplemented from vanilla replacing original values with local values
     */
    @ModifyArg(method = "onButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<World, BlockPos> overwriteApplyEnchantmentsLogic(BiConsumer<World, BlockPos> original, @Local(argsOnly = true) PlayerEntity player, @Local(argsOnly = true) int slotId, @Local(ordinal = 1) int lapisCountToDecrement) {
        return (world, blockPos) -> {

            ItemStack targetItemStack = this.inventory.getStack(0);
            ItemStack lapislazuliStack = this.inventory.getStack(1);

            // determine strategy to apply enchantments
            BiConsumer<ItemStack, EnchantmentLevelEntry> applyEnchantmentsStrategy = (itemStack, enchantmentLevelEntry) -> itemStack.addEnchantment(enchantmentLevelEntry.enchantment(), enchantmentLevelEntry.level());
            // apply enchantments
            for (EnchantmentLevelEntry entry : this.fixed_minecraft__enchantments[slotId]) {
                applyEnchantmentsStrategy.accept(targetItemStack, entry);
            }

            boolean isSuper = false;
            List<EnchantmentLevelEntry> enchantments;
            enchantments = this.generateEnchantments(world.getRegistryManager(), targetItemStack, slotId,  this.enchantmentPower[slotId]);
            for (EnchantmentLevelEntry enchantment : enchantments) {
                int i = enchantment.level();
                if (i > enchantment.enchantment().value().getMaxLevel()) isSuper = true;
            }
            if (isSuper) targetItemStack.getOrDefault(DataComponentTypes.REPAIR_COST, Integer.valueOf(1));
            if (player instanceof ServerPlayerEntity SPE && enchantments.size()>1) {
                Criteria.CONSUME_ITEM.trigger(SPE, Items.CHISELED_BOOKSHELF.getDefaultStack());
            }


            // apply enchanting costs
            int enchantingCosts = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(targetItemStack, true);
            int newLapisCost = (int)Math.ceil(enchantingCosts/10.0);
            player.applyEnchantmentCosts(targetItemStack, newLapisCost);

            // decrement lapislazuli
            // reimplemented from vanilla replacing original values with local values

            if (!player.getAbilities().creativeMode) {
                lapislazuliStack.decrement(newLapisCost);
                if (lapislazuliStack.isEmpty()) {
                    this.inventory.setStack(1, ItemStack.EMPTY);
                }
            }

            // stats stuff
            // reimplemented from vanilla replacing original values with local values
            player.incrementStat(Stats.ENCHANT_ITEM);
            if (player instanceof ServerPlayerEntity) {
                Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, targetItemStack, newLapisCost);
            }

            // lifecycle stuff
            // entirely reimplemented from vanilla
            this.inventory.markDirty();
            this.seed.set(player.getEnchantingTableSeed());
            this.onContentChanged(this.inventory);
            world.playSound(null, blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);

            for(BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
                ChiseledBookshelfBlockEntity chiseledBookShelfEntity = (ChiseledBookshelfBlockEntity) world.getBlockEntity(blockPos.add(offset));

                if (chiseledBookShelfEntity == null) {
                    continue;
                }
                if (chiseledBookShelfEntity.isEmpty()) {
                    continue;
                }
                for (int slot = 0;slot<6;slot++) {
                    ItemStack book = chiseledBookShelfEntity.getStack(slot);
                    if (book.isOf(Items.ENCHANTED_BOOK)) {
                        book.set(DataComponentTypes.REPAIR_COST, 0);
                    }
                }
            }
        };
    }

    @ModifyExpressionValue(method = "onButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"))
    private int newLapisCost(int value, @Local(ordinal = 1) int i) {
        int newLapisCost = (int)Math.ceil(this.enchantmentPower[i-1]/10.0);
        return value+i-newLapisCost;
    }
}
