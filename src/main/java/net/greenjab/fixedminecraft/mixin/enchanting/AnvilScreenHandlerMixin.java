package net.greenjab.fixedminecraft.mixin.enchanting;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;
    @Unique
    private int repairItemUsage;

    @Shadow
    private @Nullable String newItemName;

    public AnvilScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    /**
     * Makes the anvil costs solely based on the enchantment power held by the output item.
     */
    @Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
    private void calculateCost(CallbackInfo ci) {
        boolean netherite;
        int capNum = 500;
        if (!player.getWorld().isClient) {
            netherite = player.getCommandTags().contains("netherite_anvil");
        } else {
            netherite = FixedMinecraft.netheriteAnvil;
        }

        ItemStack firstInputStack = this.input.getStack(0);
        ItemStack secondInputStack = this.input.getStack(1);
        ItemStack outputItemStack = firstInputStack.copy();

        int enchantmentCapacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(outputItemStack);

        //nothing in first slot
        if (firstInputStack.isEmpty()) {
            this.levelCost.set(0);
            this.output.setStack(0, ItemStack.EMPTY);
            ci.cancel();
            return;
        }

        boolean isGold = firstInputStack.isIn(ItemTags.PIGLIN_LOVED);

        boolean newName = false;
        boolean repair = false;
        if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
            if (!this.newItemName.equals(firstInputStack.getName().getString())) {
                newName = true;
                outputItemStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
            }
        } else if (firstInputStack.contains(DataComponentTypes.CUSTOM_NAME)) {
            newName = true;
            outputItemStack.remove(DataComponentTypes.CUSTOM_NAME);
        }

        if (secondInputStack.isEmpty()) {
            this.levelCost.set(1 + capNum*enchantmentCapacity);
            if (newName) {
                this.output.setStack(0, outputItemStack);
            } else {
                this.output.setStack(0, ItemStack.EMPTY);
                this.levelCost.set(capNum * enchantmentCapacity);
            }
            ci.cancel();
            return;
        }

        //item can't have enchants
        if (!EnchantmentHelper.canHaveEnchantments(firstInputStack)) {
            this.levelCost.set(0);
            this.output.setStack(0, ItemStack.EMPTY);
            ci.cancel();
            return;
        }

        this.levelCost.set(1 + capNum*enchantmentCapacity);
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(outputItemStack));
        this.repairItemUsage = 0;
        if (!secondInputStack.isEmpty()) {
            boolean book2 = secondInputStack.contains(DataComponentTypes.STORED_ENCHANTMENTS);
            //2nd slot are ingots

            List<Item> stringRepair = List.of(new Item[]{Items.BOW, Items.CROSSBOW, Items.FISHING_ROD});

            boolean canRepair = false;
            if (secondInputStack.isOf(Items.NETHERITE_SCRAP)) {
                canRepair = outputItemStack.getItem().canRepair(firstInputStack, Items.NETHERITE_INGOT.getDefaultStack());
            } else if (secondInputStack.isOf(Items.NETHERITE_INGOT)) {
                canRepair = false;
            } else if (firstInputStack.isOf(Items.TRIDENT)) {
                canRepair = secondInputStack.isOf(Items.PRISMARINE_SHARD);
            } else if (stringRepair.contains(secondInputStack.getItem())) {
                canRepair = secondInputStack.isOf(Items.STRING);}
            else {
                canRepair = outputItemStack.getItem().canRepair(firstInputStack, secondInputStack);
            }

            if (outputItemStack.isDamageable() && canRepair) {
                int k = Math.min(outputItemStack.getDamage(), outputItemStack.getMaxDamage() / 2);
                if (k <= 0) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(capNum * enchantmentCapacity);
                    ci.cancel();
                    return;
                }

                int m;
                for (m = 0; k > 0 && m < secondInputStack.getCount(); m++) {
                    int n = outputItemStack.getDamage() - k;
                    outputItemStack.setDamage(n);
                    k = Math.min(outputItemStack.getDamage(), outputItemStack.getMaxDamage() / 2);
                }
                repair = true;
                this.repairItemUsage = m;
            } else {
                //2nd slot isnt usable
                if (!book2 && (!outputItemStack.isOf(secondInputStack.getItem()) || !outputItemStack.isDamageable())) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(capNum * enchantmentCapacity);
                    ci.cancel();
                    return;
                }

                if (outputItemStack.isDamageable() && !book2) {
                    if (!secondInputStack.contains(DataComponentTypes.ENCHANTMENTS)) {
                        int kx = firstInputStack.getMaxDamage() - firstInputStack.getDamage();
                        int m = secondInputStack.getMaxDamage() - secondInputStack.getDamage();
                        int n = m + outputItemStack.getMaxDamage() * 12 / 100;
                        int o = kx + n;
                        int p = outputItemStack.getMaxDamage() - o;
                        if (p < 0) {
                            p = 0;
                        }

                        if (p < outputItemStack.getDamage()) {
                            outputItemStack.setDamage(p);
                        }
                    }
                }

                if (book2) {
                    ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(secondInputStack);
                    boolean hasGoodEnchant = false;
                    boolean hasBadEnchant = false;

                    for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
                        RegistryEntry<Enchantment> registryEntry = entry.getKey();
                        int q = builder.getLevel(registryEntry);
                        int r = entry.getIntValue();
                        r = q == r ? r + 1 : Math.max(r, q);
                        Enchantment enchantment = registryEntry.value();
                        if (q == r) {
                            if (r < enchantment.getMaxLevel()) {
                                r = r + 1;
                            }
                        }
                        else {
                            r = Math.max(r, q);
                        }

                        boolean canAdd = enchantment.isAcceptableItem(firstInputStack);
                        if (this.player.getAbilities().creativeMode || firstInputStack.isOf(Items.ENCHANTED_BOOK)) {
                            canAdd = true;
                        }

                        for (RegistryEntry<Enchantment> registryEntry2 : builder.getEnchantments()) {
                            if (!registryEntry2.equals(registryEntry) && !Enchantment.canBeCombined(registryEntry, registryEntry2)) {
                                canAdd = false;
                            }
                        }

                        if (r > enchantment.getMaxLevel() && !isGold) {
                            if (!netherite) {
                                this.levelCost.set(1 + capNum*enchantmentCapacity);
                                this.output.setStack(0, ItemStack.EMPTY);
                                ci.cancel();
                                return;
                            }
                        }


                        if (!canAdd) {
                            hasBadEnchant = true;
                        }
                        else {
                            hasGoodEnchant = true;
                            builder.set(registryEntry, r);
                        }
                    }


                    if (hasBadEnchant && !hasGoodEnchant) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(capNum * enchantmentCapacity);
                        ci.cancel();
                        return;
                    }
                }
            }
        }
        EnchantmentHelper.set(outputItemStack, builder.build());
        if (netherite) {
            ItemEnchantmentsComponent outputEnchants = EnchantmentHelper.getEnchantments(outputItemStack);
            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : outputEnchants.getEnchantmentEntries()) {
                RegistryEntry<Enchantment> registryEntry = entry.getKey();
                if (registryEntry.getIdAsString().toLowerCase().contains("mending")) {
                    builder.set(registryEntry, 0);
                    EnchantmentHelper.set(outputItemStack, builder.build());
                }
            }
            if (secondInputStack.isOf(Items.ENCHANTED_BOOK)) {
                ItemEnchantmentsComponent bookEnchants = EnchantmentHelper.getEnchantments(secondInputStack);
                if (bookEnchants.getEnchantments().size() == 1) {
                    for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : bookEnchants.getEnchantmentEntries()) {
                        RegistryEntry<Enchantment> registryEntry = entry.getKey();
                        if (registryEntry.getIdAsString().toLowerCase().contains("mending")) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            ci.cancel();
                            return;
                        }
                    }
                }
            }
        }
        int enchantmentPower = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(outputItemStack, true);
        if (repair) this.levelCost.set((int)Math.ceil(enchantmentPower/2.0f) + capNum*enchantmentCapacity);
        else this.levelCost.set(enchantmentPower + capNum*enchantmentCapacity);

        if (!netherite) {
            if (!this.player.getAbilities().creativeMode) {
                boolean isSuper = false;
                if (outputItemStack.getComponents().contains(DataComponentTypes.REPAIR_COST)) {
                    isSuper = outputItemStack.getComponents().get(DataComponentTypes.REPAIR_COST).intValue() ==1;
                }

                if (outputItemStack.isIn(ItemTags.PIGLIN_LOVED)) {
                    isSuper = false;
                }

                if ((enchantmentPower < 1 || enchantmentCapacity < enchantmentPower) && enchantmentCapacity != 0 || isSuper) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    ci.cancel();
                    return;
                }
            }
        }
        if (!newName && outputItemStack.isOf(Items.ENCHANTED_BOOK)) {
            outputItemStack.remove(DataComponentTypes.REPAIR_COST);
        }
        this.output.setStack(0, outputItemStack);
        ci.cancel();
    }

    ItemStack anvilHolder = ItemStack.EMPTY;

    @Inject(method = "canTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void canTake(PlayerEntity playerEntity, boolean present, CallbackInfoReturnable<Boolean> cir){
        int levelCost = this.levelCost.get();
        while (levelCost>=500)levelCost-=500;
        if (!this.output.getStack(0).isEmpty())
            anvilHolder = this.output.getStack(0).copy();
        cir.setReturnValue (playerEntity.getAbilities().creativeMode || playerEntity.experienceLevel >= Math.abs(levelCost) && Math.abs(levelCost) > 0);
    }

    @Inject(method = "onTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void onTake(PlayerEntity player, ItemStack stack, CallbackInfo ci){
        if (stack.isEmpty()) stack = anvilHolder;
        int levelCost = this.levelCost.get();
        while (levelCost>=500)levelCost-=500;
        if (!player.getAbilities().creativeMode) {
            player.addExperienceLevels(-Math.abs(levelCost));
        }

        int superEnchants = 0;
        ItemEnchantmentsComponent map = EnchantmentHelper.getEnchantments(stack);
        for (RegistryEntry<Enchantment> enchantment : map.getEnchantments()) {
            int l1 = map.getLevel(enchantment);
            boolean isGold = stack.isIn(ItemTags.PIGLIN_LOVED);
            if (l1 > enchantment.value().getMaxLevel() && !isGold) {
                superEnchants++;
            }
        }

        boolean netherite;
        if (!player.getWorld().isClient()) {
            netherite = player.getCommandTags().contains("netherite_anvil");
        } else {
            netherite = FixedMinecraft.netheriteAnvil;
        }
        int cost = 12;
        if (netherite) {
            cost = levelCost;
            int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(stack);
            if (cost > cap){
                cost = 5+cost-cap;
            } else {
                cost = 0;
            }
        }
        final int finalCost = cost + 5*superEnchants;

        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.input.getStack(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.decrement(this.repairItemUsage);
                this.input.setStack(1, itemStack);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
            if (player instanceof ServerPlayerEntity SPE) {
                Criteria.CONSUME_ITEM.trigger(SPE, Items.ANVIL.getDefaultStack());
                if (netherite) {
                    boolean isSuper = false;
                    if (stack.getComponents().contains(DataComponentTypes.REPAIR_COST)) {
                        isSuper = stack.getComponents().get(DataComponentTypes.REPAIR_COST).intValue() ==1;
                    }
                    if (isSuper) {
                        Criteria.CONSUME_ITEM.trigger(SPE, ItemRegistry.NETHERITE_ANVIL.getDefaultStack());
                    }
                }
            }
        } else {
            this.input.setStack(1, ItemStack.EMPTY);
        }

        this.context.run((world, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            if (!player.getAbilities().creativeMode && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat()*100 < finalCost) {
                BlockState blockState2 = AnvilBlock.getLandingState(blockState);
                if (blockState2 == null) {
                    world.removeBlock(pos, false);
                    world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
                } else {
                    world.setBlockState(pos, blockState2, Block.NOTIFY_LISTENERS);
                    world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                }
            } else {
                world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
            }

        });
        this.input.setStack(0, ItemStack.EMPTY);
        ci.cancel();
    }
}
