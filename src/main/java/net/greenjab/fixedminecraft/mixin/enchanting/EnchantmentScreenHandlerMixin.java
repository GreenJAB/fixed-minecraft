package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Unique
    private int[] fixed_minecraft__inputEnchantmentPowers;

    // private static void debugPrintEnchantments(List<EnchantmentLevelEntry> enchantmentLevelEntryList, String before) {
    //     Map<Enchantment, Integer> enchantmentLevelMap = new HashMap<>();
    //     enchantmentLevelEntryList.forEach(levelEntry -> {
    //         enchantmentLevelMap.put(levelEntry.enchantment, levelEntry.level);
    //     });
    //     System.out.println(before + enchantmentLevelMap);
    // }

    @Inject(method = "generateEnchantments", at = @At("RETURN"), cancellable = true)
    private void generateOnlyOneEnchantment(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        // System.out.println("--- mixin generateEnchantments");
        List<EnchantmentLevelEntry> originalReturnValue = cir.getReturnValue();
        // List<EnchantmentLevelEntry> randomEnchant = List.of(originalReturnValue.get(this.random.nextInt(originalReturnValue.size())));
        List<EnchantmentLevelEntry> enchantments = new ArrayList<>(List.of(originalReturnValue.get(this.random.nextInt(originalReturnValue.size()))));
        // debugPrintEnchantments(enchantments, "input enchantments");

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

                // apply enchantments
                Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.get(itemStackAtRandomSlot);
                bookEnchantments.forEach((enchantment, lvl) -> {
                    // System.out.println("enchantment " + enchantment.toString());
                    if (!enchantment.isAcceptableItem(stack) || enchantments.stream().anyMatch(enchantmentLevelEntry -> enchantmentLevelEntry.enchantment == enchantment) /* Loops through the enchantments at each successful scan. Maybe fix later, remove doubles in the end. However, this way it's clearer */) {
                        // System.out.println("item " + stack.toString() + " is not compatible");
                        return;
                    }
                    enchantments.add(new EnchantmentLevelEntry(enchantment, lvl));
                    // System.out.println("added enchantment " + enchantment.toString());
                });
            }

        });
        // cir.setReturnValue(randomEnchant);
        // debugPrintEnchantments(enchantments, "output enchantments ");
        // System.out.println("------- end mixin generateEnchantments");
        cir.setReturnValue(enchantments);
    }

    /**
     * Rewrite logic for applying enchantments
     */
    @ModifyArg(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<World, BlockPos> generateEntries(BiConsumer<World, BlockPos> original, @Local ItemStack itemStack) {
         this.fixed_minecraft__inputEnchantmentPowers = new int[this.enchantmentPower.length];
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
                List<EnchantmentLevelEntry> enchantments = this.generateEnchantments(itemStack, slot, power);

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

                // set power for display purposes
                this.enchantmentPower[slot] = enchantmentPower;

                // set enchantment power for onButtonClick in order to correctly recreate enchantments
                this.fixed_minecraft__inputEnchantmentPowers[slot] = power;
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
     * Copies values from this.inputEnchantmentPowers to make net.minecraft.screen.EnchantmentScreenHandler.generateEnchantments(ItemStack, int, int) generate the same enchantments as in preview
     * Kinda hacky; this is a workaround to avoid reimplementing a bunch of existing logic. It would be much easier if Mojang saved the rolled enchantments
     */
    @Inject(method = "onButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private void setEnchantmentPowerToInputValues(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
        for (int slot=0; slot<this.enchantmentPower.length; slot++) {
            this.enchantmentPower[slot] = this.fixed_minecraft__inputEnchantmentPowers[slot];
        }
    }

    /**
     * Applies the rest of the enchanting costs based on the enchanted item
     * Again, kinda hacky; this is a workaround to avoid reimplementing a bunch of existing logic. It would be much easier if Mojang saved the rolled enchantments
     */
    @Inject(method = "onButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V", shift = At.Shift.AFTER))
    private void applyEnchantingCosts(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) int i) {
        // System.out.println("onButtonClick context.run Shift.AFTER; i reference " + i);
        player.applyEnchantmentCosts(this.inventory.getStack(0), FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(this.inventory.getStack(0)) - i);
    }
}
