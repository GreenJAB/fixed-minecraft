package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin extends ScreenHandler {

    protected EnchantmentScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Shadow
    protected abstract List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level);

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

    // private static void debugPrintEnchantments(List<EnchantmentLevelEntry> enchantmentLevelEntryList, String before) {
    //     Map<Enchantment, Integer> enchantmentLevelMap = new HashMap<>();
    //     enchantmentLevelEntryList.forEach(levelEntry -> {
    //         enchantmentLevelMap.put(levelEntry.enchantment, levelEntry.level);
    //     });
    //     System.out.println(before + enchantmentLevelMap);
    // }

    /**
     * Change logic how enchantments are generated.
     * <br>
     * 1. Takes the first entry of the randomly generated list. Since it already is randomly sorted there is no need to pick a random one from the list
     * 2. Checks for an accessible chiseled bookshelf nearby as often as there are regular bookshelves (limit 15).
     * 3. Chooses a random slot and adds the enchantments to the output list only if the slot contains an enchanted book and the enchantment is suitable for the item in the enchanting table
     */
    @Inject(method = "generateEnchantments", at = @At("RETURN"), cancellable = true)
    private void generateEnchantmentsWithChiseledBookshelves(ItemStack stack, int slot, int xpLevel, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        // System.out.println("--- mixin generateEnchantments");
        List<EnchantmentLevelEntry> originalReturnValue = cir.getReturnValue();

        // debugPrintEnchantments(originalReturnValue, "input enchantments");

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
                // System.out.println("scan " + i + "/" + (bookShelfCount-1));

                // choose random index and check accessibility
                int randomIndex = this.random.nextInt(EnchantingTableBlock.POWER_PROVIDER_OFFSETS.size());
                if (!FixedMinecraftEnchantmentHelper.canAccessBlock(world, tablePos, EnchantingTableBlock.POWER_PROVIDER_OFFSETS.get(randomIndex), Blocks.CHISELED_BOOKSHELF)) {
                    continue;
                }
                // System.out.println("chiseled bookshelf accessible, blockPos offset: " + EnchantingTableBlock.POWER_PROVIDER_OFFSETS.get(randomIndex).toString());

                ChiseledBookshelfBlockEntity chiseledBookShelfEntity = (ChiseledBookshelfBlockEntity) world.getBlockEntity(tablePos.add(EnchantingTableBlock.POWER_PROVIDER_OFFSETS.get(randomIndex)));

                if (chiseledBookShelfEntity == null) {
                    // System.out.println("chiseled bookshelf is null");
                    continue;
                }
                if (chiseledBookShelfEntity.isEmpty()) {
                    // System.out.println("chiseled bookshelf is empty");
                    continue;
                }

                // boolean[] isEnchantedBook = new boolean[chiseledBookShelfEntity.size()];
                // for (int stackIndex = 0; stackIndex < isEnchantedBook.length; stackIndex++) {
                //     if (chiseledBookShelfEntity.getStack(stackIndex).isOf(Items.ENCHANTED_BOOK)) {
                //         // System.out.println("stack " + stackIndex + " is EnchantedBook");
                //         isEnchantedBook[stackIndex] = true;
                //     }
                // }
                // System.out.println("slots is enchanted book: " + Arrays.toString(isEnchantedBook));

                // choose random slot
                int nextRoll = this.random.nextInt(chiseledBookShelfEntity.size());
                ItemStack itemStackAtRandomSlot = chiseledBookShelfEntity.getStack(nextRoll);

                // System.out.println("chose " + nextRoll + ". slot");

                // check whether ItemStack is not null nor empty and is enchanted book
                if (itemStackAtRandomSlot == null || itemStackAtRandomSlot.isEmpty() || !itemStackAtRandomSlot.isOf(Items.ENCHANTED_BOOK)) {
                    // System.out.println("random slot null or empty or not of enchanted book");
                    continue;
                }

                chosenItemStacks.add(itemStackAtRandomSlot);
            }
        });

        // take random enchantment out of result
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        EnchantmentLevelEntry randomEntry = originalReturnValue.get(this.random.nextInt(originalReturnValue.size()));
        enchantments.put(randomEntry.enchantment, randomEntry.level);

        // apply enchantments
        AtomicInteger enchPower = new AtomicInteger();
        enchPower.addAndGet(FixedMinecraftEnchantmentHelper.getEnchantmentPower(randomEntry.enchantment, randomEntry.level));

        for (ItemStack chosenStack : chosenItemStacks) {
            Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.get(chosenStack);
            Map<Enchantment, Integer> enchantments2 = new HashMap<>();
            bookEnchantments.forEach((enchantment, level) -> {
                // System.out.println("enchantment " + enchantment.toString() + ", level " + level);

                // ensure enchantment fits on item
                //if (!enchantment.isAcceptableItem(stack.getItem() instanceof HorseArmorItem ?Items.DIAMOND_BOOTS.getDefaultStack():stack)) {
                if (!FixedMinecraftEnchantmentHelper.horseArmorCheck(enchantment, stack.getItem())) {
                    // System.out.println("item " + stack.toString() + " is not compatible");
                    return;
                }
                // ensure highest level found is applied; thanks to the map's behaviour, no enchantment will appear more than once
                if (enchantments.containsKey(enchantment) && enchantments.get(enchantment) >= level) {
                    return;
                }
                // prevent negative or 0 enchantment power
                if ((enchPower.get() + FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, level)) <= 0) {
                    return;
                }

                enchantments2.put(enchantment, level);





                // System.out.println("added enchantment " + enchantment.toString() + " at level " + level);
            });
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

        boolean isGold = stack.isIn(ItemTags.PIGLIN_LOVED);

        // wrap in list and return
        List<EnchantmentLevelEntry> enchantmentsResult = new ArrayList<>();
        enchantments.forEach((enchantment, level) -> {
            enchantmentsResult.add(new EnchantmentLevelEntry(enchantment, (isGold&&enchantment.getMaxLevel()!=1)?level+1:level));
        });
        // debugPrintEnchantments(enchantments, "output enchantments ");
        // System.out.println("------- end mixin generateEnchantments");
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
        // System.out.println("## onConentChanged mixin");

        return ((world, blockPos) -> {

            // System.out.println("--- onContentChanged generateEntries");

            // count nearby bookshelves
            int bookShelfCount = FixedMinecraftEnchantmentHelper.countAccessibleBookshelves(world, blockPos);
            int power = (int)((FixedMinecraftEnchantmentHelper.POWER_WHEN_MAX_LEVEL-1) * bookShelfCount / 15f + 1);

            // generate enchantments for each slot
            for (int slot = 0; slot < 3; slot++) {
                // System.out.println("power " + power);

                // generate enchantments
                // System.out.println("generateEnchantments - onContentChanged on logical " + (world.isClient ? "client" : "server"));
                List<EnchantmentLevelEntry> enchantments = this.generateEnchantments(Items.AIR.getDefaultStack(), slot, power);;
                if (!itemStack.isOf(Items.BOOK)) {
                    enchantments = this.generateEnchantments(itemStack, slot, power);
                }

                // Map<Enchantment, Integer> eMap = new HashMap<>();
                // enchantments.forEach((enchantmentLevelEntry -> {eMap.put(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);}));
                // System.out.println("enchantments from generateEnchantments: " + eMap);

                // set displayed enchantment
                EnchantmentLevelEntry displayedEnchantment = enchantments.get(0);
                this.enchantmentId[slot] = Registries.ENCHANTMENT.getRawId(displayedEnchantment.enchantment); // the one that's being displayed
                this.enchantmentLevel[slot] = displayedEnchantment.level; // again, for display purposes only

                // calculate enchantment power
                int enchantmentPower = 0;
                for (EnchantmentLevelEntry entry : enchantments) {
                    enchantmentPower += FixedMinecraftEnchantmentHelper.getEnchantmentPower(entry.enchantment, entry.level);
                }

                boolean isGold = itemStack.isIn(ItemTags.PIGLIN_LOVED);
                if (isGold) enchantmentPower/=2;

                // set power for display purposes
                this.enchantmentPower[slot] = enchantmentPower;

                // save generated enchantments
                this.fixed_minecraft__enchantments[slot] = enchantments;
            }

            // System.out.println("Custom generated Entries: ");
            // System.out.println("enchantmentIds =" + Arrays.toString(this.enchantmentId));
            // System.out.println("enchantmentLevels =" + Arrays.toString(this.enchantmentLevel));
            // System.out.println("enchantmentPowers =" + Arrays.toString(this.enchantmentPower));
            // System.out.println("----------");
            // // for (int slot=0; slot<3; slot++) {
            // //     System.out.println("enchantmentId=" + this.enchantmentId[slot]);
            // //     System.out.println("enchantmentLevel=" + this.enchantmentLevel[slot]);
            // //     System.out.println("enchantmentPower=" + this.enchantmentPower[slot]);
            // // }

            // send changes
            this.sendContentUpdates();
            // System.out.println("## END onContentChanged mixin");
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
            // System.out.println("slotId: " + slotId);
            // System.out.println("lapisCountToDecrement: " + lapisCountToDecrement);

            // set book to enchanted book
            if (this.inventory.getStack(0).isOf(Items.BOOK)) {
                this.inventory.setStack(0, new ItemStack(Items.ENCHANTED_BOOK));
            }

            ItemStack targetItemStack = this.inventory.getStack(0);
            ItemStack lapislazuliStack = this.inventory.getStack(1);

            // determine strategy to apply enchantments
            BiConsumer<ItemStack, EnchantmentLevelEntry> applyEnchantmentsStrategy;
            applyEnchantmentsStrategy = (itemStack, enchantmentLevelEntry) -> {
                itemStack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
            };
            // apply enchantments
            for (EnchantmentLevelEntry entry : this.fixed_minecraft__enchantments[slotId]) {
                applyEnchantmentsStrategy.accept(targetItemStack, entry);
            }

            boolean isSuper = false;
            Map<Enchantment, Integer> map = EnchantmentHelper.get(targetItemStack);
            Iterator iter = map.keySet().iterator();
            while(iter.hasNext()) {
                Enchantment enchantment = (Enchantment)iter.next();
                int i = (Integer)map.get(enchantment);
                if (i > enchantment.getMaxLevel())isSuper = true;
            }

            if (isSuper) targetItemStack.getOrCreateSubNbt("Super");
            if (player instanceof ServerPlayerEntity SPE && map.size()>1) {
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
            this.seed.set(player.getEnchantmentTableSeed());
            this.onContentChanged(this.inventory);
            world.playSound(null, blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
        };
    }

    @ModifyExpressionValue(method = "onButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"))
    private int newLapisCost(int value, @Local(ordinal = 1) int i) {
        int newLapisCost = (int)Math.ceil(this.enchantmentPower[i-1]/10.0);
        return value+i-newLapisCost;
    }
}
