package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
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

import java.util.Iterator;
import java.util.Map;


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
        boolean netherite = false;
        if (!player.getWorld().isClient) {
            netherite = player.getCommandTags().contains("netherite_anvil");
        } else {
            netherite = this.levelCost.get()<0;
        }

        ItemStack firstInputStack = this.input.getStack(0);
        ItemStack secondInputStack = this.input.getStack(1);
        ItemStack outputItemStack = firstInputStack.copy();

        if (firstInputStack.isEmpty()) {
            this.levelCost.set(0);
            this.output.setStack(0, ItemStack.EMPTY);
            this.sendContentUpdates();
            ci.cancel();
            return;
        }

        boolean newName = false;
        if (this.newItemName != null && !Util.isBlank(this.newItemName)) {
            if (!this.newItemName.equals(firstInputStack.getName().getString())) {
                outputItemStack.setCustomName(Text.literal(this.newItemName));
                newName = true;
            }
        } else if (firstInputStack.hasCustomName()) {
            outputItemStack.removeCustomName();
            newName = true;
        }
        this.repairItemUsage = 0;


        if (secondInputStack.isEmpty()) {
            this.levelCost.set(netherite?-1:1);
            if (newName) {
                this.output.setStack(0, outputItemStack);
            } else {
                this.output.setStack(0, ItemStack.EMPTY);
            }
            this.sendContentUpdates();
            ci.cancel();
            return;
        } else {
            if (secondInputStack.isOf(Items.ENCHANTED_BOOK)) {
                Map<Enchantment, Integer> map = EnchantmentHelper.get(outputItemStack);
                Map<Enchantment, Integer> map2 = EnchantmentHelper.get(secondInputStack);
                boolean bl2 = false;
                boolean bl3 = false;
                Iterator<Enchantment> var23 = map2.keySet().iterator();

                label159:
                while(true) {
                    Enchantment enchantment;
                    do {
                        if (!var23.hasNext()) {
                            if (bl3 && !bl2) {
                                this.levelCost.set(netherite?-1:1);
                                this.output.setStack(0, ItemStack.EMPTY);
                                this.sendContentUpdates();
                                ci.cancel();
                                return;
                            }
                            break label159;
                        }

                        enchantment = var23.next();
                    } while(enchantment == null);

                    int q = map.getOrDefault(enchantment, 0);
                    int r = map2.get(enchantment);
                    if (q == r) {
                        if (r < enchantment.getMaxLevel()){
                            r = r + 1;
                        }
                    } else {
                        r = Math.max(r, q);
                    }
                    boolean bl4 = FixedMinecraftEnchantmentHelper.horseArmorCheck(enchantment, outputItemStack.getItem());//enchantment.isAcceptableItem(outputItemStack);
                    if (this.player.getAbilities().creativeMode || outputItemStack.isOf(Items.ENCHANTED_BOOK)) {
                        bl4 = true;
                    }

                    for (Enchantment enchantment2 : map.keySet()) {
                        if (enchantment2 != enchantment && !enchantment.canCombine(enchantment2)) {
                            bl4 = false;
                        }
                    }

                    if (!bl4) {
                        bl3 = true;
                    } else {
                        bl2 = true;
                         map.put(enchantment, r);
                    }
                }
                EnchantmentHelper.set(map, outputItemStack);

                boolean isSame = true;
                Map<Enchantment, Integer> newMap1 = EnchantmentHelper.get(outputItemStack);
                Map<Enchantment, Integer> newMap2 = EnchantmentHelper.get(firstInputStack);
                for (Enchantment enchantment : newMap1.keySet()) {
                    boolean hasSameEnchant = false;
                    int l1 = newMap1.get(enchantment);

                    boolean isGold = firstInputStack.isIn(ItemTags.PIGLIN_LOVED);
                    if (l1 > enchantment.getMaxLevel() && !isGold) {
                        if (!netherite) {
                            this.levelCost.set(1);
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.sendContentUpdates();
                            ci.cancel();
                            return;
                        }
                    }

                    if (newMap2.containsKey(enchantment)) {
                        int l2 =  newMap2.get(enchantment);
                        if (l1 == l2) {
                            hasSameEnchant = true;
                        }
                    }
                    if (!hasSameEnchant) isSame = false;
                }
                if (isSame) {
                    this.levelCost.set(netherite?-1:1);
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    ci.cancel();
                    return;
                }
            } else if (secondInputStack.isOf(firstInputStack.getItem())) {
                if (secondInputStack.hasEnchantments() || firstInputStack.getDamage()==0) {
                    this.levelCost.set(netherite?-1:1);
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    ci.cancel();
                    return;
                }
                if (outputItemStack.isDamageable()) {
                    int l = firstInputStack.getMaxDamage() - firstInputStack.getDamage();
                    int m = secondInputStack.getMaxDamage() - secondInputStack.getDamage();
                    int n = m + outputItemStack.getMaxDamage() * 12 / 100;
                    int o = l + n;
                    int p = outputItemStack.getMaxDamage() - o;
                    if (p < 0) {
                        p = 0;
                    }

                    if (p < outputItemStack.getDamage()) {
                        outputItemStack.setDamage(p);
                    }
                }
            } else if (outputItemStack.isDamageable() && outputItemStack.getItem().canRepair(firstInputStack, secondInputStack)) {
                int l = Math.min(outputItemStack.getDamage(), outputItemStack.getMaxDamage() / 2);
                if (l <= 0) {
                    this.levelCost.set(netherite?-1:1);
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    ci.cancel();
                    return;
                }
                int m = 0;
                for(m = 0; l > 0 && m < secondInputStack.getCount(); ++m) {
                    int n = outputItemStack.getDamage() - l;
                    outputItemStack.setDamage(n);
                    l = Math.min(outputItemStack.getDamage(), outputItemStack.getMaxDamage() / 4);
                }
                this.repairItemUsage = m;

            } else {
                this.levelCost.set(0);
                this.output.setStack(0, ItemStack.EMPTY);
                this.sendContentUpdates();
                ci.cancel();
                return;
            }
        }


        if (netherite) {
            Map<Enchantment, Integer> map = EnchantmentHelper.get(outputItemStack);
            if (map.containsKey(Enchantments.MENDING)) {
                map.remove(Enchantments.MENDING);
                EnchantmentHelper.set(map, outputItemStack);

                if (secondInputStack.isOf(Items.ENCHANTED_BOOK)) {
                    Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.get(secondInputStack);
                    if (bookEnchants.size()==1) {
                        if (bookEnchants.containsKey(Enchantments.MENDING)) {
                            this.output.setStack(0, ItemStack.EMPTY);
                            this.sendContentUpdates();
                            ci.cancel();
                            return;
                        }
                    }
                }
            }
        }


        // calculate enchantmentPower for each enchantment
        int enchantmentPower = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(outputItemStack, true);

        if (netherite) {
            this.levelCost.set(-enchantmentPower);
        } else {
           this.levelCost.set(enchantmentPower);
           // check if item can hold combined enchantment power
           if (!this.player.getAbilities().creativeMode) {
               int enchantmentCapacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(outputItemStack);

               boolean isSuper = firstInputStack.getNbt().contains("Super");
               if (outputItemStack.isIn(ItemTags.PIGLIN_LOVED)) {
                   isSuper = false;
               }

               if ((enchantmentPower < 1 || enchantmentCapacity < enchantmentPower)&&enchantmentCapacity!=0 || isSuper) {
                    this.output.setStack(0, ItemStack.EMPTY);
                   this.sendContentUpdates();
                   ci.cancel();
                   return;
                }
           }
        }


        this.output.setStack(0, outputItemStack);
        this.sendContentUpdates();
        ci.cancel();
    }

    @Inject(method = "canTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void canTake(PlayerEntity playerEntity, boolean present, CallbackInfoReturnable<Boolean> cir){
        AnvilScreenHandler ASH = (AnvilScreenHandler)(Object)this;
        cir.setReturnValue (playerEntity.getAbilities().creativeMode || playerEntity.experienceLevel >= Math.abs(ASH.getLevelCost()) && Math.abs(ASH.getLevelCost()) > 0);
    }

    @Inject(method = "onTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void onTake(PlayerEntity player, ItemStack stack, CallbackInfo ci){
        if (!player.getAbilities().creativeMode) {
            player.addExperienceLevels(-Math.abs(this.levelCost.get()));
        }

        int superEnchants = 0;
        Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
        for (Enchantment enchantment : map.keySet()) {
            int l1 = map.get(enchantment);
            boolean isGold = stack.isIn(ItemTags.PIGLIN_LOVED);
            if (l1 > enchantment.getMaxLevel() && !isGold) {
                superEnchants++;
            }
        }

        boolean netherite = this.levelCost.get()<0;
        int cost = 0;
        if (netherite) {
            cost = -this.levelCost.get();
            int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(stack);
            if (cost > cap){
                cost = 5+cost-cap;
            } else {
                cost = 0;
            }
        } else {
            cost = 12;
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
                    if (stack.getNbt().contains("Super")) {
                        Criteria.CONSUME_ITEM.trigger(SPE, ItemRegistry.INSTANCE.getNETHERITE_ANVIL().getDefaultStack());
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
